/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
interface SqlClient {

    fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): SqlClientSelect.Select<T>

    fun <T : Any> createTable(tableClass: KClass<T>): Any

    fun createTables(vararg tableClasses: KClass<*>): Any

    fun <T : Any> insert(row: T): Any

    fun insert(vararg rows: Any): Any

    fun <T : Any> deleteFromTable(tableClass: KClass<T>): SqlClientDelete.Delete
}

/**
 * Blocking Sql Client, to be used with any blocking JDBC driver
 * @author Fred Montariol
 */
interface SqlClientBlocking : SqlClient {

    override fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): SqlClientSelectBlocking.Select<T>

    override fun <T : Any> createTable(tableClass: KClass<T>)

    override fun createTables(vararg tableClasses: KClass<*>) {
        tableClasses.forEach { tableClass -> createTable(tableClass) }
    }

    override fun <T : Any> insert(row: T)

    override fun insert(vararg rows: Any)

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): SqlClientDeleteBlocking.Delete
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SqlClientBlocking.select(
        noinline selectDsl: ((ValueProvider) -> T)? = null
) = select(T::class, selectDsl)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SqlClientBlocking.createTable() = createTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SqlClientBlocking.deleteFromTable() = deleteFromTable(T::class)


private fun tableMustBeMapped(tableName: String?) = "Requested table \"$tableName\" is not mapped"

@Suppress("UNCHECKED_CAST")
fun <T : Any> Tables.getTable(tableClass: KClass<out T>): Table<T> =
        this.allTables[tableClass] as Table<T>?
                ?: throw IllegalArgumentException(tableMustBeMapped(tableClass.qualifiedName))

fun <T : Any> Tables.checkTable(tableClass: KClass<out T>) {
    require(this.allTables.containsKey(tableClass)) { tableMustBeMapped(tableClass.qualifiedName) }
}

fun Tables.checkTables(tableClasses: Array<out KClass<*>>) {
    tableClasses.forEach { tableClass -> checkTable(tableClass) }
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
            logger.debug { "Exec SQL : INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($valuesDebug)" }
        }
        return "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($values)"
    }
}

internal fun logValue(value: Any?) = when (value) {
    null -> "null"
    is String -> "\'$value\'"
    is LocalDate -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_DATE)}\'"
    is LocalDateTime -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}\'"
    is Instant -> "\'${DateTimeFormatter.ISO_INSTANT.format(value)}\'"
    is LocalTime -> "\'${value.format(DateTimeFormatter.ISO_LOCAL_TIME)}\'"
    is Boolean -> "$value"
    else -> throw RuntimeException("should never happen")
}
