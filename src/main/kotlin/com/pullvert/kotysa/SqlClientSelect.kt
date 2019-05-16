/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
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
        fun where(whereDsl: WhereDsl<T>.(WhereColumnPropertyProvider) -> WhereClause<*, *>): Where<T>
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
    internal interface SelectProperties<T : Any> {
        val tables: Tables
        val resultClass: KClass<T>
        val transform: ((ValueProvider) -> T)?
    }

    internal interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        val tables: Tables
        val resultClass: KClass<T>
        val transform: ((ValueProvider) -> T)?
    }

    internal interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    @Suppress("UNCHECKED_CAST")
    internal interface Return<T : Any> : SqlClientSelect.Return<T> {
        val selectProperties: SelectProperties<T>

        fun getSelectInformation() = with(selectProperties) {
            if (transform != null) {
                SelectDsl(transform!!, tables.allColumns).initialize()
            } else {
                selectInformationForSingleClass(resultClass)
            }
        }

        fun <T> selectSql(selectInformation: SelectInformation<T>): String = with(selectInformation) {
            val fields = selectedFields.joinToString { field -> field.fieldName }
            val tables = selectedTables.joinToString { table -> table.name }
            val selectSql = "SELECT $fields FROM $tables"
            logger.debug { "Exec SQL : $selectSql" }
            return selectSql
        }

        private fun selectInformationForSingleClass(resultClass: KClass<T>): SelectInformation<T> {
            val table = selectProperties.tables.allTables[resultClass] as Table<T>
            var fieldIndex = 0
            val columnPropertyIndexMap = mutableMapOf<KProperty1<*, *>, Int>()
            val selectedFields = mutableListOf<Field>()
            table.columns.forEach { (property, column) ->
                columnPropertyIndexMap[property] = fieldIndex++
                val field = when (property.returnType.withNullability(false)) {
                    String::class.createType() -> StringColumnField(column)
                    LocalDateTime::class.createType() -> LocalDateTimeColumnField(column)
                    Date::class.createType() -> DateColumnField(column)
                    else -> throw RuntimeException("should never happen")
                }
                selectedFields.add(field)
            }
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
                                Date::class.createType() -> args[param] = it[prop as KProperty1<T, Date?>]
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
}
