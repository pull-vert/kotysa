/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientSelectR2dbc private constructor() : DefaultSqlClientSelect() {

    internal class Select<T : Any> internal constructor(
            override val client: DatabaseClient,
            tables: Tables,
            resultClass: KClass<T>,
            dsl: (SelectDslApi.(ValueProvider) -> T)?
    ) : DefaultSqlClientSelect.Select<T>(tables, resultClass, dsl), ReactorSqlClientSelect.Select<T>, Return<T> {

        override fun where(whereDsl: WhereDsl.(FieldProvider) -> WhereClause): ReactorSqlClientSelect.Where<T> {
            val where = Where(client, properties)
            where.addWhereClause(whereDsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientSelect.Where<T>, ReactorSqlClientSelect.Where<T>, Return<T>

    private interface Return<T : Any> : DefaultSqlClientSelect.Return<T>, ReactorSqlClientSelect.Return<T> {

        val client: DatabaseClient

        override fun fetchOne() = fetch().one()
        override fun fetchFirst() = fetch().first()
        override fun fetchAll() = fetch().all()

        private fun fetch() = with(properties) {
            var executeSpec = client.execute()
                    .sql(selectSql())
            whereClauses
                    .mapNotNull { whereClause -> whereClause.value }
                    .forEachIndexed { index, value ->
                        executeSpec = executeSpec.bind(index, value)
                    }

            executeSpec.map { r, _ ->
                val row = R2dbcRow(r, selectInformation.fieldIndexMap)
                selectInformation.select(row, row)
            }
        }

        @Suppress("UNCHECKED_CAST")
        private class R2dbcRow(
                private val r2bcRow: Row,
                fieldIndexMap: Map<Field, Int>
        ) : AbstractRow(fieldIndexMap) {
            override fun <T> get(index: Int, type: Class<T>) = r2bcRow.get(index, type) as T
        }
    }
}
