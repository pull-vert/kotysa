/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
private class CoroutinesSqlClientR2Dbc internal constructor(
        client: DatabaseClient,
        tables: Tables
) : CoroutinesSqlClient() {

    private val delegate = SqlClientR2dbc(client, tables)

    @ExperimentalStdlibApi
    override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T> =
            CoroutineSqlClientSelectR2dbc.Select(delegate.select(resultClass, dsl))

    override suspend fun <T : Any> createTable(tableClass: KClass<T>) {
        delegate.createTable(tableClass).awaitFirstOrNull()
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T> =
            CoroutineSqlClientDeleteR2dbc.Delete(delegate.deleteFromTable(tableClass))

    override fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
            CoroutineSqlClientUpdateR2dbc.Update(delegate.updateTable(tableClass))

    override suspend fun <T : Any> insert(row: T) {
        delegate.insert(row).awaitFirstOrNull()
    }

    override suspend fun insert(vararg rows: Any) {
        delegate.insert(*rows).awaitFirstOrNull()
    }
}

/**
 * @author Fred Montariol
 */
private class CoroutineSqlClientSelectR2dbc private constructor() {
    internal class Select<T : Any> internal constructor(
            override val delegate: ReactorSqlClientSelect.Select<T>
    ) : CoroutinesSqlClientSelect.Select<T>(), Whereable<T>, Return<T> {
        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType,
                                      dsl: (FieldProvider) -> ColumnField<*, *>): CoroutinesSqlClientSelect.Join<T> =
                Join(delegate.joinOn(joinClass, alias, type, dsl))
    }

    private interface Whereable<T : Any> : CoroutinesSqlClientSelect.Whereable<T> {
        val delegate: ReactorSqlClientSelect.Whereable<T>

        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): CoroutinesSqlClientSelect.Where<T> =
                Where(delegate.where(dsl))
    }

    private class Join<T : Any> internal constructor(
            override val delegate: ReactorSqlClientSelect.Join<T>
    ) : CoroutinesSqlClientSelect.Join<T>, Whereable<T>, Return<T>

    private class Where<T : Any> internal constructor(
            override val delegate: ReactorSqlClientSelect.Where<T>
    ) : CoroutinesSqlClientSelect.Where<T>, Return<T>

    private interface Return<T : Any> : CoroutinesSqlClientSelect.Return<T> {
        val delegate: ReactorSqlClientSelect.Return<T>

        override suspend fun fetchOne(): T =
                try {
                    delegate.fetchOne().awaitSingle()
                } catch (_: NoSuchElementException) {
                    throw NoResultException()
                }

        override suspend fun fetchOneOrNull() = delegate.fetchOne().awaitFirstOrNull()

        override suspend fun fetchFirst(): T =
                try {
                    delegate.fetchFirst().awaitFirst()
                } catch (_: NoSuchElementException) {
                    throw NoResultException()
                }

        override suspend fun fetchFirstOrNull() = delegate.fetchFirst().awaitFirstOrNull()

        @ExperimentalCoroutinesApi
        override fun fetchAll() = delegate.fetchAll().asFlow()
    }
}

/**
 * @author Fred Montariol
 */
private class CoroutineSqlClientDeleteR2dbc private constructor() {
    internal class Delete<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.DeleteOrUpdate<T>
    ) : CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T>(), Return {
        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType,
                                      dsl: (FieldProvider) -> ColumnField<*, *>): CoroutinesSqlClientDeleteOrUpdate.Join<T> =
            Join(delegate.joinOn(joinClass, alias, type, dsl))

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T> =
                TypedWhere(delegate.where(dsl))
    }

    private class Join<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.Join
    ) : CoroutinesSqlClientDeleteOrUpdate.Join<T>, Return {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.Where =
                Where(delegate.where(dsl))
    }

    private class Where internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.Where
    ) : CoroutinesSqlClientDeleteOrUpdate.Where, Return

    private class TypedWhere<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.TypedWhere<T>
    ) : CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T>, Return

    private interface Return : CoroutinesSqlClientDeleteOrUpdate.Return {
        val delegate: ReactorSqlClientDeleteOrUpdate.Return

        override suspend fun execute(): Int = delegate.execute().awaitSingle()
    }
}

/**
 * @author Fred Montariol
 */
private class CoroutineSqlClientUpdateR2dbc private constructor() {
    internal class Update<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.Update<T>
    ) : CoroutinesSqlClientDeleteOrUpdate.Update<T>(), Return {

        override fun set(dsl: (FieldSetter<T>) -> Unit): CoroutinesSqlClientDeleteOrUpdate.Update<T> {
            delegate.set(dsl)
            return this
        }

        override fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType,
                                      dsl: (FieldProvider) -> ColumnField<*, *>): CoroutinesSqlClientDeleteOrUpdate.Join<T> =
                Join(delegate.joinOn(joinClass, alias, type, dsl))

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T> =
                TypedWhere(delegate.where(dsl))
    }

    private class Join<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.Join
    ) : CoroutinesSqlClientDeleteOrUpdate.Join<T>, Return {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): CoroutinesSqlClientDeleteOrUpdate.Where =
                Where(delegate.where(dsl))
    }

    private class Where internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.Where
    ) : CoroutinesSqlClientDeleteOrUpdate.Where, Return

    private class TypedWhere<T : Any> internal constructor(
            override val delegate: ReactorSqlClientDeleteOrUpdate.TypedWhere<T>
    ) : CoroutinesSqlClientDeleteOrUpdate.TypedWhere<T>, Return

    private interface Return : CoroutinesSqlClientDeleteOrUpdate.Return {
        val delegate: ReactorSqlClientDeleteOrUpdate.Return

        override suspend fun execute(): Int = delegate.execute().awaitSingle()
    }
}

/**
 * Create a [CoroutinesSqlClient] from a R2DBC [DatabaseClient] with [Tables] mapping
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
fun DatabaseClient.coSqlClient(tables: Tables): CoroutinesSqlClient = CoroutinesSqlClientR2Dbc(this, tables)
