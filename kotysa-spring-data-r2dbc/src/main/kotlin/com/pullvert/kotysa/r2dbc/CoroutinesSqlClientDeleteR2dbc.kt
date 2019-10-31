/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class CoroutinesSqlClientDeleteR2dbc private constructor() : AbstractSqlClientDeleteR2dbc() {

    internal class Delete<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T>(), DeleteOrUpdate<T>, Return<T> {
        override val properties: Properties<T> = initProperties()

        override fun <U : Any> join(joinClass: KClass<U>, alias: String?, type: JoinType): CoroutinesSqlClientDeleteOrUpdate.Joinable =
                Joinable(client, properties, joinClass, alias, type)

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T> {
            val where = TypedWhere(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Joinable<T : Any, U : Any> internal constructor(
            private val client: DatabaseClient,
            private val properties: Properties<T>,
            private val joinClass: KClass<U>,
            private val alias: String?,
            private val type: JoinType
    ) : CoroutinesSqlClientDeleteOrUpdate.Joinable {

        override fun on(dsl: (FieldProvider) -> ColumnField<*, *>): CoroutinesSqlClientDeleteOrUpdate.Join {
            val join = Join(client, properties)
            join.addJoinClause(dsl, joinClass, alias, type)
            return join
        }
    }

    private class Join<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Join<T>, CoroutinesSqlClientDeleteOrUpdate.Join, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.Where<T>, CoroutinesSqlClientDeleteOrUpdate.Where, Return<T>

    private class TypedWhere<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDeleteOrUpdate.TypedWhere<T>, CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T>, Return<T>

    private interface Return<T : Any> : AbstractSqlClientDeleteR2dbc.Return<T>, CoroutinesSqlClientDeleteOrUpdate.Return {

        override suspend fun execute(): Int = fetch().rowsUpdated().awaitSingle() // fixme replace when https://github.com/spring-projects/spring-data-r2dbc/issues/212
    }
}
