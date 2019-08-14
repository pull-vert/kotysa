/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * Reactive (using Reactor Mono and Flux) Sql Client, to be used with R2dbc
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
abstract class ReactorSqlClient : SqlClient {

    abstract fun <T : Any> insert(row: T): Mono<Void>

    abstract fun insert(vararg rows: Any): Mono<Void>

    @PublishedApi
    internal abstract fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T>

    @PublishedApi
    internal abstract fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void>

    @PublishedApi
    internal abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): ReactorSqlClientDelete.Delete<T>

    @PublishedApi
    internal abstract fun <T : Any> updateTable(tableClass: KClass<T>): ReactorSqlClientUpdate.Update<T>
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.select(noinline dsl: SelectDslApi.(ValueProvider) -> T) = select(T::class, dsl)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.select() = select(T::class, null)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.selectAll() = select(T::class, null).fetchAll()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.countAll() = select(Long::class) { count<T>() }.fetchOne()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.createTable() = createTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.deleteFromTable() = deleteFromTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.deleteAllFromTable() = deleteFromTable(T::class).execute()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.updateTable() = updateTable(T::class)


/**
 * @author Fred Montariol
 */
class ReactorSqlClientSelect private constructor() {

    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        /**
         * This Query return one result as [Mono], or an empty [Mono] if no result
         *
         * @throws NonUniqueResultException if more than one result
         */
        fun fetchOne(): Mono<T>
        /**
         * This Query return one result as [Mono], or an empty [Mono] if no result
         */
        fun fetchFirst(): Mono<T>
        /**
         * This Query can return several results as [Flux], or an empty [Flux] if no result
         */
        fun fetchAll(): Flux<T>
    }
}

/**
 * @author Fred Montariol
 */
class ReactorSqlClientDelete private constructor() {

    interface Delete<T : Any> : SqlClientDelete.Delete<T>, Return {
        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientDelete.Where, Return

    interface Return : SqlClientDelete.Return {
        /**
         * Execute delete and return the number of deleted rows
         */
        fun execute(): Mono<Int>
    }
}

/**
 * @author Fred Montariol
 */
class ReactorSqlClientUpdate private constructor() {

    interface Update<T : Any> : SqlClientUpdate.Update<T>, Return {
        override fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientUpdate.Where, Return

    interface Return : SqlClientUpdate.Return {
        /**
         * Execute update and return the number of updated rows
         */
        fun execute(): Mono<Int>
    }
}
