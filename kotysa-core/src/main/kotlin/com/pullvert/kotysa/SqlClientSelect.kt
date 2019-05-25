/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

/**
 * @author Fred Montariol
 */
open class SqlClientSelect private constructor() {
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
open class DefaultSqlClientSelect protected constructor() {
    class SelectProperties<T : Any>(
            val tables: Tables,
            val selectInformation: SelectInformation<T>,
            val whereClauses: MutableList<WhereClause>,
            internal val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    )

    abstract class Select<T : Any> protected constructor(
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
            // build availableColumns Map
            val availableColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()
            selectInformation.selectedTables
                    .flatMap { table -> table.columns.entries }
                    .forEach { columnEntry ->
                        // 1) add mapped getters
                        @Suppress("UNCHECKED_CAST")
                        availableColumns[columnEntry.key as (Any) -> Any?] = columnEntry.value
                        // 2) todo add overridden parent getters
                    }
            selectProperties = SelectProperties(tables, selectInformation, mutableListOf(), availableColumns)
        }

        @Suppress("UNCHECKED_CAST")
        private fun selectInformationForSingleClass(resultClass: KClass<T>, tables: Tables): SelectInformation<T> {
            val table = tables.allTables[resultClass] as Table<T>
            var fieldIndex = 0
            val columnPropertyIndexMap = mutableMapOf<(Any) -> Any?, Int>()

            // build selectedFields List
            val selectedFields = mutableListOf<Field>()
            table.columns.forEach { (getter, _) ->
                columnPropertyIndexMap[getter as (Any) -> Any?] = fieldIndex++
                val getterType = getter.toCallable().returnType
                val field = when (getterType.withNullability(false)) {
                    String::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullStringColumnField(tables.allColumns, getter as (Any) -> String)
                        } else {
                            NullableStringColumnField(tables.allColumns, getter as (Any) -> String?)
                        }
                    LocalDateTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalDateTimeColumnField(tables.allColumns, getter as (Any) -> LocalDateTime)
                        } else {
                            NullableLocalDateTimeColumnField(tables.allColumns, getter as (Any) -> LocalDateTime?)
                        }
                    LocalDate::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalDateColumnField(tables.allColumns, getter as (Any) -> LocalDate)
                        } else {
                            NullableLocalDateColumnField(tables.allColumns, getter as (Any) -> LocalDate?)
                        }
                    Instant::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullInstantColumnField(tables.allColumns, getter as (Any) -> Instant)
                        } else {
                            NullableInstantColumnField(tables.allColumns, getter as (Any) -> Instant?)
                        }
                    LocalTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalTimeColumnField(tables.allColumns, getter as (Any) -> LocalTime)
                        } else {
                            NullableLocalTimeColumnField(tables.allColumns, getter as (Any) -> LocalTime?)
                        }
                    Boolean::class.createType() -> {
                        require(!getterType.isMarkedNullable) { "$getter is nullable, Boolean must not be nullable" }
                        NotNullBooleanColumnField(tables.allColumns, getter as (Any) -> Boolean)
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
                        val getter = table.columns.keys.firstOrNull { getter -> getter.toCallable().name == param.name }
                        if (getter != null) {
                            when (getter.toCallable().returnType.withNullability(false)) {
                                String::class.createType() -> args[param] = it[getter as (Any) -> String?]
                                LocalDateTime::class.createType() -> args[param] = it[getter as (Any) -> LocalDateTime?]
                                LocalDate::class.createType() -> args[param] = it[getter as (Any) -> LocalDate?]
                                Instant::class.createType() -> args[param] = it[getter as (Any) -> Instant?]
                                LocalTime::class.createType() -> args[param] = it[getter as (Any) -> LocalTime?]
                                Boolean::class.createType() -> args[param] = it[getter as (Any) -> Boolean]
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

    protected interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T> {

        fun addWhereClause(dsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause) {
            selectProperties.apply {
                whereClauses.add(WhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    protected interface Return<T : Any> : SqlClientSelect.Return<T> {
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

/**
 * @author Fred Montariol
 */
data class SelectInformation<T>(
        val columnPropertyIndexMap: Map<out (Any) -> Any?, Int>,
        internal val selectedFields: List<Field>,
        internal val selectedTables: Set<Table<*>>,
        val select: (ValueProvider) -> T
)
