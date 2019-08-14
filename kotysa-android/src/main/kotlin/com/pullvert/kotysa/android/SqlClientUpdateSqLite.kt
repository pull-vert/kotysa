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
internal class SqlClientUpdateSqLite private constructor() : DefaultSqlClientUpdate() {

    internal class Update<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientUpdate.Update<T>(tables, tableClass), BlockingSqlClientUpdate.Update<T>, Return<T> {

        override fun set(dsl: (FieldSetter<T>) -> Unit): BlockingSqlClientUpdate.Update<T> {
            addSetValue(dsl)
            return this
        }

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): BlockingSqlClientUpdate.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientUpdate.Where<T>, BlockingSqlClientUpdate.Where, Return<T>

    private interface Return<T : Any> : DefaultSqlClientUpdate.Return<T>, BlockingSqlClientUpdate.Return {
        val client: SQLiteDatabase

        override fun execute() = with(properties) {
            val contentValues = ContentValues(setValues.size)
            setValues.forEach { (column, value) -> contentValues.put(column.name, value) }

            var whereParams: Array<String>? = null
            var whereClauseStr: String? = null
            if (whereClauses.isNotEmpty()) {
                val buildedWhereClause = whereClause(whereClauses)
                if (buildedWhereClause.isNotEmpty()) {
                    // remove 'WHERE '
                    whereClauseStr = buildedWhereClause.substring(6)
                }
                whereParams = whereClauses
                        .mapNotNull { whereClause ->
                            whereClause.value
                        }
                        .map { whereValue -> stringValue(whereValue).replace("\'", "") }
                        .toTypedArray()
            }
            // debug query
            updateTableSqlDebug()

            client.update(table.name, contentValues, whereClauseStr, whereParams)
        }
    }
}
