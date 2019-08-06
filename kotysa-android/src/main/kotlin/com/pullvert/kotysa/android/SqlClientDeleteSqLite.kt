/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientDeleteSqLite private constructor() : DefaultSqlClientDelete() {

    internal class Delete<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientDelete.Delete<T>(tables, tableClass), BlockingSqlClientDelete.Delete<T>, Return<T> {

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): BlockingSqlClientDelete.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientDelete.Where<T>, BlockingSqlClientDelete.Where, Return<T>

    private interface Return<T : Any> : DefaultSqlClientDelete.Return<T>, BlockingSqlClientDelete.Return {
        val client: SQLiteDatabase

        override fun execute() = with(properties) {
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
            deleteFromTableSqlDebug()

            client.delete(table.name, whereClauseStr, whereParams)
        }
    }
}
