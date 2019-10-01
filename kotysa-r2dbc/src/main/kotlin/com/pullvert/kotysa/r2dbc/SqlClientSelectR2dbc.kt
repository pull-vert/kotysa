/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import io.r2dbc.spi.Row
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientSelectR2dbc private constructor() : DefaultSqlClientSelect() {

    @ExperimentalStdlibApi
    internal class Select<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val tables: Tables,
            override val resultClass: KClass<T>,
            override val dsl: (SelectDslApi.(ValueProvider) -> T)?
    ) : ReactorSqlClientSelect.Select<T>(), DefaultSqlClientSelect.Select<T>, Whereable<T>, Return<T> {
        override val properties: Properties<T> = initProperties()

        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType, dsl: (FieldProvider) -> ColumnField<*, *>): ReactorSqlClientSelect.Join<T> {
            val join = Join(client, properties)
            join.addJoinClause(dsl, joinClass, alias, type)
            return join
        }
    }

    private interface Whereable<T : Any> : DefaultSqlClientSelect.Whereable<T>, ReactorSqlClientSelect.Whereable<T> {
        val client: DatabaseClient

        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): ReactorSqlClientSelect.Where<T> {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Join<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientSelect.Join<T>, ReactorSqlClientSelect.Join<T>, Whereable<T>, Return<T>

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientSelect.Where<T>, ReactorSqlClientSelect.Where<T>, Return<T>

    private interface Return<T : Any> : DefaultSqlClientSelect.Return<T>, ReactorSqlClientSelect.Return<T> {

        val client: DatabaseClient

        override fun fetchOne(): Mono<T> = fetch().one()
                .onErrorMap(IncorrectResultSizeDataAccessException::class.java) { NonUniqueResultException() }

        override fun fetchFirst(): Mono<T> = fetch().first()
        override fun fetchAll(): Flux<T> = fetch().all()

        private fun fetch() = with(properties) {
            var executeSpec = client.execute(selectSql())

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
