/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KLogger
import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.allSuperclasses

/**
 * @author Fred Montariol
 */
interface SqlClient


private fun tableMustBeMapped(tableName: String?) = "Requested table \"$tableName\" is not mapped"

@Suppress("UNCHECKED_CAST")
fun <T : Any> Tables.getTable(tableClass: KClass<out T>): Table<T> =
        this.allTables[tableClass] as Table<T>?
                ?: throw IllegalArgumentException(tableMustBeMapped(tableClass.qualifiedName))

fun <T : Any> Tables.checkTable(tableClass: KClass<out T>) {
    require(this.allTables.containsKey(tableClass)) { tableMustBeMapped(tableClass.qualifiedName) }
}

private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
interface DefaultSqlClient : SqlClient {
    val tables: Tables

    fun createTableSql(tableClass: KClass<*>): String {
        val table = tables.getTable(tableClass)
        var primaryKey: String? = null
        val columns = table.columns.values.joinToString { column ->
            if (column.isPrimaryKey) {
                primaryKey = "CONSTRAINT pk_${table.name} PRIMARY KEY (${column.name})"
            }
            val nullability = if (column.isNullable) "NULL" else "NOT NULL"
            "${column.name} ${column.sqlType.fullType} $nullability"
        }
        val createTableSql = "CREATE TABLE IF NOT EXISTS ${table.name} ($columns, $primaryKey)"
        logger.debug { "Exec SQL : $createTableSql" }
        return createTableSql
    }

    fun <T : Any> insertSql(row: T): String {
        val table = tables.getTable(row::class)
        val columnNames = mutableSetOf<String>()
        val values = table.columns.values.joinToString { column ->
            columnNames.add(column.name)
            "?"
        }
        if (logger.isDebugEnabled) {
            val valuesDebug = table.columns.values.joinToString { column ->
                val columnValue = column.entityGetter(row)
                logValue(columnValue)
            }
            logger.debug("Exec SQL : INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($valuesDebug)")
        }
        return "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($values)"
    }
}

private fun logValue(value: Any?) = when (value) {
    null -> "null"
    is String -> "\'$value\'"
    is LocalDate -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_DATE)}\'"
    is LocalDateTime -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}\'"
    is Instant -> "\'${DateTimeFormatter.ISO_INSTANT.format(value)}\'"
    is LocalTime -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_TIME)}\'"
    is Boolean -> "$value"
    is UUID -> "\'$value\'"
    else -> throw RuntimeException("${value.javaClass.canonicalName} is not supported yet")
}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientCommon protected constructor() {

    protected interface Properties {
        val tables: Tables
        val whereClauses: MutableList<WhereClause>
        val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    }

    abstract class Instruction protected constructor() {
        @Suppress("UNCHECKED_CAST")
        protected fun <T : Any> addAvailableColumnsFromTable(
                availableColumns: MutableMap<(Any) -> Any?, Column<*, *>>,
                table: Table<T>
        ) {
            table.columns.forEach { (key, value) ->
                // 1) add mapped getter
                availableColumns[key as (Any) -> Any?] = value

                val getterCallable = key.toCallable()

                // 2) add overridden parent getters associated with this column
                table.tableClass.allSuperclasses
                        .flatMap { superClass -> superClass.members }
                        .filter { callable ->
                            callable.isAbstract
                                    && callable.name == getterCallable.name
                                    && (callable is KProperty1<*, *> || callable.name.startsWith("get"))
                                    && (callable.returnType == getterCallable.returnType
                                    || callable.returnType.classifier is KTypeParameter)
                        }
                        .forEach { callable ->
                            availableColumns[callable as (Any) -> Any?] = value
                        }
            }
        }
    }

    protected interface Where : Return {
        fun addWhereClause(dsl: WhereDsl.(FieldProvider) -> WhereClause) {
            properties.apply {
                whereClauses.add(WhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    protected interface TypedWhere<T : Any> : Return {
        fun addWhereClause(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause) {
            properties.apply {
                whereClauses.add(TypedWhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    protected interface Return {
        val properties: Properties

        fun stringValue(value: Any?) = logValue(value)

        private fun whereClause(whereClauses: MutableList<WhereClause>) =
                if (whereClauses.isEmpty()) {
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
                            else -> throw UnsupportedOperationException("${whereClause.operation} is not supported yet")
                        }
                    }
                }

        fun whereClauseDebug(whereClauses: MutableList<WhereClause>, logger: KLogger) =
                if (whereClauses.isEmpty() || !logger.isDebugEnabled) {
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
                            else -> throw UnsupportedOperationException("${whereClause.operation} is not supported yet")
                        }
                    }
                }

        fun whereAndWhereDebug(whereClauses: MutableList<WhereClause>, logger: KLogger): Pair<String, String> =
                Pair(whereClause(whereClauses), whereClauseDebug(whereClauses, logger))
    }
}
