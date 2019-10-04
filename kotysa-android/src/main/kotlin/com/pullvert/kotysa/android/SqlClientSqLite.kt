/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.android.sample.UserRepositorySqLite
 * @author Fred Montariol
 */
internal class SqlClientSqLite(
        private val client: SQLiteOpenHelper,
        override val tables: Tables
) : BlockingSqlClient(), DefaultSqlClient {

    @ExperimentalStdlibApi
    override fun <T : Any> select(resultClass: KClass<T>,
                                  dsl: (SelectDslApi.(ValueProvider) -> T)?): BlockingSqlClientSelect.Select<T> =
            SqlClientSelectSqLite.Select(client.readableDatabase, tables, resultClass, dsl)

    override fun <T : Any> createTable(tableClass: KClass<T>) {
        val createTableSql = createTableSql(tableClass)
        return client.writableDatabase.execSQL(createTableSql)
    }

    override fun <T : Any> insert(row: T) {
        val table = tables.getTable(row::class)
        val contentValues = ContentValues(table.columns.size)
        table.columns.values
                .forEach { column -> contentValues.put(column.name, column.entityGetter(row)) }

        // debug query
        insertSqlDebug(row)

        client.writableDatabase.insert(table.name, null, contentValues)
    }

    override fun insert(vararg rows: Any) {
        // fail-fast : check that all tables are mapped Tables
        rows.forEach { row -> tables.checkTable(row::class) }

        rows.forEach { row -> insert(row) }
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): BlockingSqlClientDeleteOrUpdate.DeleteOrUpdate<T> =
            SqlClientDeleteSqLite.Delete(client.writableDatabase, tables, tableClass)

    override fun <T : Any> updateTable(tableClass: KClass<T>): BlockingSqlClientDeleteOrUpdate.Update<T> =
            SqlClientUpdateSqLite.Update(client.writableDatabase, tables, tableClass)
}

internal fun ContentValues.put(name: String, value: Any?) {
    if (value != null) {
        when (value) {
            is Int -> put(name, value)
            is Byte -> put(name, value)
            is Long -> put(name, value)
            is Float -> put(name, value)
            is Short -> put(name, value)
            is Double -> put(name, value)
            is String -> put(name, value)
            is Boolean -> put(name, value)
            is ByteArray -> put(name, value)
            else -> throw UnsupportedOperationException(
                    "${value.javaClass.canonicalName} is not supported by Android SqLite")
        }
    } else {
        putNull(name)
    }
}

/**
 * Create a [BlockingSqlClient] from a Android SqLite [SQLiteDatabase] with [Tables] mapping
 *
 * @sample com.pullvert.kotysa.android.sample.UserRepositorySqLite
 * @author Fred Montariol
 */
fun SQLiteOpenHelper.sqlClient(tables: Tables): BlockingSqlClient = SqlClientSqLite(this, tables)
