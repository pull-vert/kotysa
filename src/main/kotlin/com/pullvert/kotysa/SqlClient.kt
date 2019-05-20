/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
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
): SqlClientSelect.Select<T> = select(T::class, selectDsl)

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
internal fun <T : Any> Tables.getTable(tableClass: KClass<out T>): Table<T> =
        this.allTables[tableClass] as Table<T>?
                ?: throw IllegalArgumentException(tableMustBeMapped(tableClass.qualifiedName))

internal fun <T : Any> Tables.checkTable(tableClass: KClass<out T>) {
    require(this.allTables.containsKey(tableClass)) { tableMustBeMapped(tableClass.qualifiedName) }
}

internal fun Tables.checkTables(tableClasses: Array<out KClass<*>>) {
    tableClasses.forEach { tableClass -> checkTable(tableClass) }
}

private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
internal interface DefaultSqlClient : SqlClient {
    val tables: Tables

    fun createTableSql(tableClass: KClass<*>): String {
        val table = tables.getTable(tableClass)
        var primaryKey: String? = null
        val columns = table.columns.values.joinToString { column ->
            if (column.isPrimaryKey) {
                primaryKey = "CONSTRAINT pk_${table.name} PRIMARY KEY (${column.name})"
            }
            val nullability = if (column.isNullable) "NULL" else "NOT NULL"
            "${column.name} ${column.sqlType} $nullability"
        }
        val createTableSql = "CREATE TABLE IF NOT EXISTS ${table.name} ($columns, $primaryKey)"
        logger.debug { "Exec SQL : $createTableSql" }
        return createTableSql
    }

    fun <T : Any> insertSql(row: T): String {
        val table = tables.getTable(row::class)
        val columnNames = mutableSetOf<String>()
        val values = mutableListOf<Any?>()
        table.columns.values.forEach { column ->
            columnNames.add(column.name)
            val columnValue = column.entityProperty.get(row)
            val value = if (columnValue == null) {
                null
            } else {
                when (column.sqlType) {
                    SqlType.VARCHAR -> "'$columnValue'"
                    else -> throw IllegalArgumentException("${column.sqlType} is not handled yet for insert")
                }
            }
            values.add(value)
        }
        // todo change this to use binded params !
        val insertSql = "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES (${values.joinToString()})"
        logger.debug { "Exec SQL : $insertSql" }
        return insertSql
    }
}
