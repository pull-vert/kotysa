/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientDeleteR2dbc private constructor() : DefaultSqlClientDeleteOrUpdate() {

    internal class Delete<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : ReactorSqlClientDeleteOrUpdate.DeleteOrUpdate<T>(), DeleteOrUpdate<T>, Return<T> {
        override val properties: Properties<T> = initProperties()

        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType,
                                      dsl: (FieldProvider) -> ColumnField<*, *>): ReactorSqlClientDeleteOrUpdate.Join {
            val join = Join(client, properties)
            join.addJoinClause(dsl, joinClass, alias, type)
            return join
        }

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): ReactorSqlClientDeleteOrUpdate.TypedWhere<T> {
            val where = TypedWhere(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Join<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Join<T>, ReactorSqlClientDeleteOrUpdate.Join, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): ReactorSqlClientDeleteOrUpdate.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Where<T>, ReactorSqlClientDeleteOrUpdate.Where, Return<T>

    private class TypedWhere<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.TypedWhere<T>, ReactorSqlClientDeleteOrUpdate.TypedWhere<T>, Return<T>

    private interface Return<T : Any> : DefaultSqlClientDeleteOrUpdate.Return<T>, ReactorSqlClientDeleteOrUpdate.Return {
        val client: DatabaseClient

        override fun execute(): Mono<Int> = with(properties) {
            var executeSpec = client.execute(deleteFromTableSql())

            whereClauses
                    .mapNotNull { whereClause -> whereClause.value }
                    .forEachIndexed { index, value ->
                        executeSpec = executeSpec.bind(index, value)
                    }

            executeSpec.fetch().rowsUpdated()
        }
    }
}
