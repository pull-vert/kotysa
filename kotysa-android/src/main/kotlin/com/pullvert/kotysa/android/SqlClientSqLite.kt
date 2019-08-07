/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientSqLite(
        private val client: SQLiteDatabase,
        override val tables: Tables
) : BlockingAndroidSqlClient(), DefaultSqlClient {
    override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): BlockingSqlClientSelect.Select<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any> createTable(tableClass: KClass<T>) {
        val createTableSql = createTableSql(tableClass)
        return client.execSQL(createTableSql)
    }

    override fun <T : Any> insert(row: T) {
        val table = tables.getTable(row::class)
        val contentValues = ContentValues()
        table.columns.values
                .forEach { column ->
                    val value = column.entityGetter(row)
                    if (value != null) {
                        when (value) {
                            is Int -> contentValues.put(column.name, value)
                            is Byte -> contentValues.put(column.name, value)
                            is Long -> contentValues.put(column.name, value)
                            is Float -> contentValues.put(column.name, value)
                            is Short -> contentValues.put(column.name, value)
                            is Double -> contentValues.put(column.name, value)
                            is String -> contentValues.put(column.name, value)
                            is Boolean -> contentValues.put(column.name, value)
                            is ByteArray -> contentValues.put(column.name, value)
                            else -> throw UnsupportedOperationException(
                                    "${value.javaClass.canonicalName} is not supported by Android SqLite")
                        }
                    } else {
                        contentValues.putNull(column.name)
                    }
                }

        // debug query
        insertSqlDebug(row)

        client.insert(table.name, null, contentValues)
    }

    override fun insert(vararg rows: Any) {
        // fail-fast : check that all tables are mapped Tables
        rows.forEach { row -> tables.checkTable(row::class) }

        rows.forEach { row -> insert(row) }
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): BlockingSqlClientDelete.Delete<T> =
            SqlClientDeleteSqLite.Delete(client, tables, tableClass)

    override fun <T : Any> updateTable(tableClass: KClass<T>): BlockingSqlClientUpdate.Update<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * Create a [BlockingAndroidSqlClient] from a Android SqLite [SQLiteDatabase] with [Tables] mapping
 *
 * todo @sample sample
 * @author Fred Montariol
 */
fun SQLiteDatabase.sqlClient(tables: Tables): BlockingAndroidSqlClient = SqlClientSqLite(this, tables)
