/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientDeleteSqLite private constructor() : DefaultSqlClientDeleteOrUpdate() {

    internal class Delete<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : BlockingSqlClientDeleteOrUpdate.DeleteOrUpdate<T>(), DeleteOrUpdate<T>, Return<T> {
        override val properties: Properties<T> = initProperties()

        override fun <U : Any> join(joinClass: KClass<U>, alias: String?, type: JoinType): BlockingSqlClientDeleteOrUpdate.Joinable =
                Joinable(client, properties, joinClass, alias, type)

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): BlockingSqlClientDeleteOrUpdate.TypedWhere<T> {
            val where = TypedWhere(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Joinable<T : Any, U : Any> internal constructor(
            private val client: SQLiteDatabase,
            private val properties: Properties<T>,
            private val joinClass: KClass<U>,
            private val alias: String?,
            private val type: JoinType
    ) : BlockingSqlClientDeleteOrUpdate.Joinable {

        override fun on(dsl: (FieldProvider) -> ColumnField<*, *>): BlockingSqlClientDeleteOrUpdate.Join {
            val join = Join(client, properties)
            join.addJoinClause(dsl, joinClass, alias, type)
            return join
        }
    }

    private class Join<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Join<T>, BlockingSqlClientDeleteOrUpdate.Join, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): BlockingSqlClientDeleteOrUpdate.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Where<T>, BlockingSqlClientDeleteOrUpdate.Where, Return<T>

    private class TypedWhere<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.TypedWhere<T>, BlockingSqlClientDeleteOrUpdate.TypedWhere<T>, Return<T>

    private interface Return<T : Any> : DefaultSqlClientDeleteOrUpdate.Return<T>, BlockingSqlClientDeleteOrUpdate.Return {
        val client: SQLiteDatabase

        override fun execute() = with(properties) {
            var whereParams: Array<String>? = null
            if (whereClauses.isNotEmpty()) {
                whereParams = whereClauses
                        .mapNotNull { typedWhereClause -> typedWhereClause.whereClause.value }
                        .map { whereValue -> stringValue(whereValue) }
                        .toTypedArray()
            }

            val deleteFromTableSql = deleteFromTableSql()
            val whereClause = if (deleteFromTableSql.isNotEmpty()) {
                deleteFromTableSql
            } else {
                null
            }
            client.delete(table.name, whereClause, whereParams)
        }
    }
}
