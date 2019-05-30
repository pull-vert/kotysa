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
            selectDsl: ((ValueProvider) -> T)?
    ) : DefaultSqlClientSelect.Select<T>(tables, resultClass, selectDsl), ReactorSqlClientSelect.Select<T>, Return<T> {

        override fun where(whereDsl: WhereDsl.(WhereFieldProvider) -> WhereClause): ReactorSqlClientSelect.Where<T> {
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
                selectInformation.select.invoke(R2dbcRow(r, selectInformation.columnPropertyIndexMap))
            }
        }

        @Suppress("UNCHECKED_CAST")
        private class R2dbcRow(
                private val r2bcRow: Row,
                columnPropertyIndexMap: Map<out (Any) -> Any?, Int>
        ) : AbstractRow(columnPropertyIndexMap) {
            override fun <T> get(index: Int, type: Class<T>) = r2bcRow.get(index, type) as T
        }
    }
}
