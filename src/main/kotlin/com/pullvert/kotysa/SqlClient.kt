/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
interface AbstractSqlClient {

	fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): SqlClientSelect.AbstractSelect<T>

	fun <T : Any> createTable(tableClass: KClass<T>): Any

	fun createTables(vararg tableClasses: KClass<*>): Any

	fun <T : Any> insert(row: T): Any

	fun insert(vararg rows: Any): Any
}

/**
 * Blocking Sql Client, to be used with any blocking JDBC driver
 * @author Fred Montariol
 */
interface SqlClient : AbstractSqlClient {

	override fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): SqlClientSelect.Select<T>

	override fun <T : Any> createTable(tableClass: KClass<T>)

	override fun createTables(vararg tableClasses: KClass<*>)
//		tableClasses.forEach { tableClass -> createTable(tableClass) } move to implementation when available

	override fun <T : Any> insert(row: T)

	override fun insert(vararg rows: Any)
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SqlClient.select(
		noinline selectDsl: ((ValueProvider) -> T)? = null
): SqlClientSelect.Select<T> = select(T::class, selectDsl)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SqlClient.createTable() = createTable(T::class)


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
internal interface DefaultSqlClient : AbstractSqlClient {
	val tables: Tables

	fun <T : Any> selectCheck(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?) {
		if (selectDsl == null) {
			checkTable(resultClass)
		}
	}

	fun createTableSql(tableClass: KClass<*>): String {
		val table = getTable(tableClass)
		var primaryKey: String? = null
		val columns = table.columns.values.joinToString { column ->
			if (column.isPrimaryKey) {
				primaryKey = "CONSTRAINT pk_${table.name} PRIMARY KEY (${column.columnName})"
			}
			val nullability = if (column.isNullable) "NULL" else "NOT NULL"
			"${column.columnName} ${column.sqlType} $nullability"
		}
		val createTableSql = "CREATE TABLE IF NOT EXISTS ${table.name} ($columns, $primaryKey)"
		logger.debug { "Exec SQL : $createTableSql" }
		return createTableSql
	}

	@Suppress("UNCHECKED_CAST")
	fun <T : Any> getTable(tableClass: KClass<out T>): Table<T> =
			tables.allTables[tableClass] as Table<T>?
					?: throw IllegalArgumentException(tableMustBeMapped(tableClass.qualifiedName))

	fun <T : Any> checkTable(tableClass: KClass<out T>) {
		require(tables.allTables.containsKey(tableClass)) { tableMustBeMapped(tableClass.qualifiedName) }
	}


	fun checkTables(tableClasses: Array<out KClass<*>>) {
		tableClasses.forEach { tableClass -> checkTable(tableClass) }
	}


	fun <T : Any> insertSql(row: T): String {
		val table = getTable(row::class)
		val columnNames = mutableSetOf<String>()
		val values = mutableListOf<Any?>()
		table.columns.values.forEach { column ->
			columnNames.add(column.columnName)
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
		val insertSql = "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES (${values.joinToString()})"
		logger.debug { "Exec SQL : $insertSql" }
		return insertSql
	}

	private fun tableMustBeMapped(tableName: String?) = "Requested table \"$tableName\" is not mapped"
}
