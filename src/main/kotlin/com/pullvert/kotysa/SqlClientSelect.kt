/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

/**
 * @author Fred Montariol
 */
class SqlClientSelect private constructor() {
    interface Select<T : Any> : Return<T> {
        fun where(whereDsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : Return<T>

    interface Return<T : Any>
}

/**
 * @author Fred Montariol
 */
class SqlClientSelectBlocking private constructor() {
    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        fun fetchOne(): T
        fun fetchAll(): List<T>
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
internal class DefaultSqlClientSelect private constructor() {
    internal class SelectProperties<T : Any>(
            val tables: Tables,
            val selectInformation: SelectInformation<T>,
            val whereClauses: MutableList<WhereClause>
    )

    internal abstract class Select<T : Any> protected constructor(
            tables: Tables,
            resultClass: KClass<T>,
            selectDsl: ((ValueProvider) -> T)?
    ) : SqlClientSelect.Select<T>, Return<T> {

        final override val selectProperties: SelectProperties<T>

        init {
            if (selectDsl == null) {
                tables.checkTable(resultClass)
            }
            val selectInformation = if (selectDsl != null) {
                SelectDsl(selectDsl, tables.allColumns).initialize()
            } else {
                selectInformationForSingleClass(resultClass, tables)
            }
            selectProperties = SelectProperties(tables, selectInformation, mutableListOf())
        }

        @Suppress("UNCHECKED_CAST")
        private fun selectInformationForSingleClass(resultClass: KClass<T>, tables: Tables): SelectInformation<T> {
            val table = tables.allTables[resultClass] as Table<T>
            var fieldIndex = 0
            val columnPropertyIndexMap = mutableMapOf<KProperty1<*, *>, Int>()

            // build selectedFields List
            val selectedFields = mutableListOf<Field>()
            table.columns.forEach { (property, _) ->
                columnPropertyIndexMap[property] = fieldIndex++
                val field = when (property.returnType.withNullability(false)) {
                    String::class.createType() ->
                        if (property.returnType.isMarkedNullable) {
                            NotNullStringColumnField(tables.allColumns, property as KProperty1<T, String>)
                        } else {
                            NullableStringColumnField(tables.allColumns, property as KProperty1<T, String?>)
                        }
                    LocalDateTime::class.createType() ->
                        if (property.returnType.isMarkedNullable) {
                            NotNullLocalDateTimeColumnField(tables.allColumns, property as KProperty1<T, LocalDateTime>)
                        } else {
                            NullableLocalDateTimeColumnField(tables.allColumns, property as KProperty1<T, LocalDateTime?>)
                        }
                    LocalDate::class.createType() ->
                        if (property.returnType.isMarkedNullable) {
                            NotNullLocalDateColumnField(tables.allColumns, property as KProperty1<T, LocalDate>)
                        } else {
                            NullableLocalDateColumnField(tables.allColumns, property as KProperty1<T, LocalDate?>)
                        }
                    Instant::class.createType() ->
                        if (property.returnType.isMarkedNullable) {
                            NotNullInstantColumnField(tables.allColumns, property as KProperty1<T, Instant>)
                        } else {
                            NullableInstantColumnField(tables.allColumns, property as KProperty1<T, Instant?>)
                        }
                    else -> throw RuntimeException("should never happen")
                }
                selectedFields.add(field)
            }

            // Build select Function : (ValueProvider) -> T
            val select: (ValueProvider) -> T = { it ->
                with(table.tableClass.primaryConstructor!!) {
                    val args = mutableMapOf<KParameter, Any?>()
                    for (param in parameters) {
                        // get the name corresponding mapped property
                        val prop = table.columns.keys.firstOrNull { property -> property.name == param.name }
                        if (prop != null) {
                            when (prop.returnType.withNullability(false)) {
                                String::class.createType() -> args[param] = it[prop as KProperty1<T, String?>]
                                LocalDateTime::class.createType() -> args[param] = it[prop as KProperty1<T, LocalDateTime?>]
                                LocalDate::class.createType() -> args[param] = it[prop as KProperty1<T, LocalDate?>]
                                Instant::class.createType() -> args[param] = it[prop as KProperty1<T, Instant?>]
                                else -> throw RuntimeException("should never happen")
                            }
                        } else {
                            require(param.isOptional) {
                                "Cannot instanciate Table \"${table.tableClass.qualifiedName}\"," +
                                        "parameter \"${param.name}\" is required and is not mapped to a Column"
                            }
                        }
                    }
                    callBy(args)
                }
            }
            return SelectInformation(columnPropertyIndexMap, selectedFields, setOf(table), select)
        }
    }

    internal interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T> {

        fun addWhereClause(dsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause) {
            selectProperties.apply {
                whereClauses.add(WhereDsl(dsl, tables.allColumns).initialize())
            }
        }
    }

    internal interface Return<T : Any> : SqlClientSelect.Return<T> {
        val selectProperties: SelectProperties<T>

        fun selectSql(): String = with(selectProperties) {
            val selects = selectInformation.selectedFields.joinToString(prefix = "SELECT ") { field -> field.fieldName }
            val froms = selectInformation.selectedTables.joinToString(prefix = "FROM ") { table -> table.name }
            val wheres = if (whereClauses.isEmpty()) {
                ""
            } else {
                whereClauses.joinToString(" AND ", "WHERE ") { whereClause ->
                    when (whereClause.operation) {
                        Operation.EQ ->
                            if (whereClause.value == null) {
                                "${whereClause.field.fieldName} IS NULL"
                            } else {
                                "${whereClause.field.fieldName} = ?"
                            }
                        else -> throw RuntimeException("should never happen")
                    }
                }
            }
            if (logger.isDebugEnabled) {
                val wheresDebug = if (whereClauses.isEmpty()) {
                    ""
                } else {
                    whereClauses.joinToString(" AND ", "WHERE ") { whereClause ->
                        when (whereClause.operation) {
                            Operation.EQ ->
                                if (whereClause.value == null) {
                                    "${whereClause.field.fieldName} IS NULL"
                                } else {
                                    "${whereClause.field.fieldName} = ${logValue(whereClause.value)}"
                                }
                            else -> throw RuntimeException("should never happen")
                        }
                    }
                }
                logger.debug { "Exec SQL : $selects $froms $wheresDebug" }
            }
            return "$selects $froms $wheres"
        }
    }
}
