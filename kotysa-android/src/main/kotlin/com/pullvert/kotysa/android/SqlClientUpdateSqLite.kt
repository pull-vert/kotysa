/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.android

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

internal class SqlClientUpdateSqLite private constructor() : DefaultSqlClientDeleteOrUpdate() {

    internal class Update<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : BlockingSqlClientDeleteOrUpdate.Update<T>(), DefaultSqlClientDeleteOrUpdate.Update<T>, Return<T> {
        override val properties: Properties<T> = initProperties()

        override fun set(dsl: (FieldSetter<T>) -> Unit): BlockingSqlClientDeleteOrUpdate.Update<T> {
            addSetValue(dsl)
            return this
        }

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
    ) : DefaultSqlClientDeleteOrUpdate.Where<T>, BlockingSqlClientDeleteOrUpdate.Where, Return<T> {

        override fun and(dsl: WhereDsl.(FieldProvider) -> WhereClause): BlockingSqlClientDeleteOrUpdate.Where {
            addAndClause(dsl)
            return this
        }

        override fun or(dsl: WhereDsl.(FieldProvider) -> WhereClause): BlockingSqlClientDeleteOrUpdate.Where {
            addOrClause(dsl)
            return this
        }
    }

    private class TypedWhere<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.TypedWhere<T>, BlockingSqlClientDeleteOrUpdate.TypedWhere<T>, Return<T> {

        override fun and(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): BlockingSqlClientDeleteOrUpdate.TypedWhere<T> {
            addAndClause(dsl)
            return this
        }

        override fun or(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): BlockingSqlClientDeleteOrUpdate.TypedWhere<T> {
            addOrClause(dsl)
            return this
        }
    }

    private interface Return<T : Any> : DefaultSqlClientDeleteOrUpdate.Return<T>, BlockingSqlClientDeleteOrUpdate.Return {
        val client: SQLiteDatabase

        override fun execute() = with(properties) {
            val contentValues = ContentValues(setValues.size)
            setValues.forEach { (column, value) -> contentValues.put(column.name, value) }

            var whereParams: Array<String>? = null
            if (whereClauses.isNotEmpty()) {
                whereParams = whereClauses
                        .mapNotNull { typedWhereClause -> typedWhereClause.whereClause.value }
                        .map { whereValue -> stringValue(whereValue) }
                        .toTypedArray()
            }

            val updateTableSql = updateTableSql()
            val whereClause = if (updateTableSql.isNotEmpty()) {
                updateTableSql
            } else {
                null
            }
            client.update(table.name, contentValues, whereClause, whereParams)
        }
    }
}
