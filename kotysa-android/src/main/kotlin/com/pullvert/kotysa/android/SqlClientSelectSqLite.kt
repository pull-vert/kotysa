/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientSelectSqLite private constructor() : DefaultSqlClientSelect() {

    @ExperimentalStdlibApi
    internal class Select<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val tables: Tables,
            override val resultClass: KClass<T>,
            override val dsl: (SelectDslApi.(ValueProvider) -> T)?
    ) : BlockingSqlClientSelect.Select<T>(), DefaultSqlClientSelect.Select<T>, Whereable<T>, Return<T> {

        override val properties: Properties<T> = initProperties()

        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType, dsl: (FieldProvider) -> ColumnField<*, *>): BlockingSqlClientSelect.Join<T> {
            val join = Join(client, properties)
            join.addJoinClause(dsl, joinClass, alias, type)
            return join
        }
    }

    private interface Whereable<T : Any> : DefaultSqlClientSelect.Whereable<T>, BlockingSqlClientSelect.Whereable<T> {
        val client: SQLiteDatabase

        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): BlockingSqlClientSelect.Where<T> {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Join<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientSelect.Join<T>, BlockingSqlClientSelect.Join<T>, Whereable<T>, Return<T>

    private class Where<T : Any> internal constructor(
            override val client: SQLiteDatabase,
            override val properties: Properties<T>
    ) : DefaultSqlClientSelect.Where<T>, BlockingSqlClientSelect.Where<T>, Return<T>

    private interface Return<T : Any> : DefaultSqlClientSelect.Return<T>, BlockingSqlClientSelect.Return<T> {
        val client: SQLiteDatabase

        override fun fetchOne() = with(properties.selectInformation) {
            val cursor = fetch()
            if (!cursor.moveToFirst()) throw NoResultException()
            if (!cursor.isLast) throw NonUniqueResultException()
            val row = SqLiteRow(cursor, fieldIndexMap)
            select(row, row)
        }

        override fun fetchOneOrNull() = with(properties.selectInformation) {
            val cursor = fetch()
            if (!cursor.moveToFirst()) {
                null
            } else {
                if (!cursor.isLast) throw NonUniqueResultException()
                val row = SqLiteRow(cursor, fieldIndexMap)
                select(row, row)
            }
        }

        override fun fetchFirst() = with(properties.selectInformation) {
            val cursor = fetch()
            if (!cursor.moveToFirst()) throw NoResultException()
            val row = SqLiteRow(cursor, fieldIndexMap)
            select(row, row)
        }

        override fun fetchFirstOrNull() = with(properties.selectInformation) {
            val cursor = fetch()
            if (!cursor.moveToFirst()) {
                null
            } else {
                val row = SqLiteRow(cursor, fieldIndexMap)
                select(row, row)
            }
        }

        override fun fetchAll() = with(properties.selectInformation) {
            val cursor = fetch()
            val row = SqLiteRow(cursor, fieldIndexMap)
            val results = mutableListOf<T>()
            while (cursor.moveToNext()) {
                results.add(select(row, row))
            }
            results
        }

        private fun fetch() = with(properties) {
            var whereParams: Array<String>? = null
            if (whereClauses.isNotEmpty()) {
                whereParams = whereClauses
                        .mapNotNull { whereClause -> whereClause.value }
                        .map { whereValue -> stringValue(whereValue) }
                        .toTypedArray()
            }

            client.rawQuery(selectSql(), whereParams)
        }

        @Suppress("UNCHECKED_CAST")
        private class SqLiteRow(
                private val sqLiteCursor: Cursor,
                fieldIndexMap: Map<Field, Int>
        ) : AbstractRow(fieldIndexMap) {
            override fun <T> get(index: Int, type: Class<T>) =
                    if (sqLiteCursor.isNull(index)) {
                        null
                    } else {
                        when {
                            Int::class.java.isAssignableFrom(type) -> sqLiteCursor.getInt(index)
                            Long::class.java.isAssignableFrom(type) -> sqLiteCursor.getLong(index)
                            Float::class.java.isAssignableFrom(type) -> sqLiteCursor.getFloat(index)
                            Short::class.java.isAssignableFrom(type) -> sqLiteCursor.getShort(index)
                            Double::class.java.isAssignableFrom(type) -> sqLiteCursor.getDouble(index)
                            String::class.java.isAssignableFrom(type) -> sqLiteCursor.getString(index)
                            // boolean is stored as Int
                            java.lang.Boolean::class.java.isAssignableFrom(type) -> sqLiteCursor.getInt(index) != 0
                            ByteArray::class.java.isAssignableFrom(type) -> sqLiteCursor.getBlob(index)
                            else -> throw UnsupportedOperationException(
                                    "${type.canonicalName} is not supported by Android SqLite")
                        } as T?
                    }
        }
    }
}
