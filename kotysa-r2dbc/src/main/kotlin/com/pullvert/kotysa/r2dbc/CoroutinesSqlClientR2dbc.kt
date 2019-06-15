/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.flow.asFlow
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * Coroutines Sql Client, to be used with R2dbc
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
abstract class CoroutinesSqlClientR2dbc : CoroutinesSqlClient {

    @PublishedApi
    internal abstract fun <T : Any> select(tableClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T>

    @PublishedApi
    internal abstract suspend fun <T : Any> createTable(tableClass: KClass<T>)

    @PublishedApi
    internal abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDelete.Delete<T>

    @PublishedApi
    internal abstract fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientUpdate.Update<T>
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> CoroutinesSqlClientR2dbc.select(noinline dsl: SelectDslApi.(ValueProvider) -> T) = select(T::class, dsl)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> CoroutinesSqlClientR2dbc.select() = select(T::class, null)

/**
 * @author Fred Montariol
 */
suspend inline fun <reified T : Any> CoroutinesSqlClientR2dbc.createTable() = createTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> CoroutinesSqlClientR2dbc.deleteFromTable() = deleteFromTable(T::class)

/**
 * @author Fred Montariol
 */
suspend inline fun <reified T : Any> CoroutinesSqlClientR2dbc.deleteAllFromTable() = deleteFromTable(T::class).execute()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> CoroutinesSqlClientR2dbc.updateTable() = updateTable(T::class)


/**
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
private class CoroutinesSqlClientR2DbcImpl(
        client: DatabaseClient,
        tables: Tables
) : CoroutinesSqlClientR2dbc() {

    private val delegate: ReactorSqlClient

    init {
        delegate = SqlClientR2dbc(client, tables)
    }

    override fun <T : Any> select(tableClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T> =
            CoroutineSqlClientSelectR2dbc.Select(delegate.select(tableClass, dsl))

    override suspend fun <T : Any> createTable(tableClass: KClass<T>) {
        delegate.createTable(tableClass).awaitFirstOrNull()
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDelete.Delete<T> =
            CoroutineSqlClientDeleteR2dbc.Delete(delegate.deleteFromTable(tableClass))

    override fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientUpdate.Update<T> =
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
    internal class Select<T : Any> internal constructor(override val delegate: ReactorSqlClientSelect.Select<T>) : CoroutinesSqlClientSelect.Select<T>, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): CoroutinesSqlClientSelect.Where<T> = Where(delegate.where(dsl))
    }

    private class Where<T : Any> internal constructor(override val delegate: ReactorSqlClientSelect.Where<T>) : CoroutinesSqlClientSelect.Where<T>, Return<T>

    private interface Return<T : Any> : CoroutinesSqlClientSelect.Return<T> {
        val delegate: ReactorSqlClientSelect.Return<T>

        override suspend fun fetchOne(): T = delegate.fetchOne().awaitSingle()
        override suspend fun fetchOneOrNull(): T? = delegate.fetchOne().awaitFirstOrNull()
        override suspend fun fetchFirst(): T = delegate.fetchFirst().awaitSingle()
        override suspend fun fetchFirstOrNull(): T? = delegate.fetchFirst().awaitFirstOrNull()
        @FlowPreview
        override fun fetchAll(batchSize: Int): Flow<T> = delegate.fetchAll().asFlow(batchSize)
    }
}

/**
 * @author Fred Montariol
 */
private class CoroutineSqlClientDeleteR2dbc private constructor() {
    internal class Delete<T : Any> internal constructor(override val delegate: ReactorSqlClientDelete.Delete<T>) : CoroutinesSqlClientDelete.Delete<T>, Return {

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): CoroutinesSqlClientDelete.Where = Where(delegate.where(dsl))
    }

    private class Where internal constructor(override val delegate: ReactorSqlClientDelete.Where) : CoroutinesSqlClientDelete.Where, Return

    private interface Return : CoroutinesSqlClientDelete.Return {
        val delegate: ReactorSqlClientDelete.Return

        override suspend fun execute(): Int = delegate.execute().awaitSingle()
    }
}

/**
 * @author Fred Montariol
 */
private class CoroutineSqlClientUpdateR2dbc private constructor() {
    internal class Update<T : Any> internal constructor(override val delegate: ReactorSqlClientUpdate.Update<T>) : CoroutinesSqlClientUpdate.Update<T>, Return {
        override fun set(dsl: (FieldSetter<T>) -> Unit): CoroutinesSqlClientUpdate.Update<T> {
            delegate.set(dsl)
            return this
        }

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): CoroutinesSqlClientUpdate.Where = Where(delegate.where(dsl))
    }

    private class Where internal constructor(override val delegate: ReactorSqlClientUpdate.Where) : CoroutinesSqlClientUpdate.Where, Return

    private interface Return : CoroutinesSqlClientUpdate.Return {
        val delegate: ReactorSqlClientUpdate.Return

        override suspend fun execute() {
            delegate.execute().awaitFirstOrNull()
        }
    }
}

/**
 * Create a [CoroutinesSqlClientR2dbc] from a R2DBC [DatabaseClient] with [Tables] mapping
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
fun DatabaseClient.coSqlClient(tables: Tables): CoroutinesSqlClientR2dbc = CoroutinesSqlClientR2DbcImpl(this, tables)
