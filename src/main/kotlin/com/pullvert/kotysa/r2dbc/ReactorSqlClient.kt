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
 * @author Fred Montariol
 */
interface ReactorSqlClient : SqlClient {

    override fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T>

    override fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void>

    override fun createTables(vararg tableClasses: KClass<*>): Mono<Void>

    override fun <T : Any> insert(row: T): Mono<Void>

    override fun insert(vararg rows: Any): Mono<Void>

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): ReactorSqlClientDelete.Delete
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> ReactorSqlClient.select(
        noinline selectDsl: ((ValueProvider) -> T)? = null
) = select(T::class, selectDsl)

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
class ReactorSqlClientSelect private constructor() {
    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        override fun <U : Any> where(
                tableClass: KClass<U>,
                whereDsl: WhereDsl<T>.(WhereColumnPropertyProvider) -> WhereClause<U, *>
        ): Where<T>
    }

    interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        fun fetchOne(): Mono<T>
        fun fetchFirst(): Mono<T>
        fun fetchAll(): Flux<T>
    }
}

/**
 * @author Fred Montariol
 */
inline fun <T : Any, reified U : Any> ReactorSqlClientSelect.Select<T>.where(
        noinline whereDsl: WhereDsl<T>.(WhereColumnPropertyProvider) -> WhereClause<U, *>
) = where(U::class, whereDsl)

/**
 * @author Fred Montariol
 */
class ReactorSqlClientDelete private constructor() {
    interface Delete : SqlClientDelete.Delete, Return

    interface Return : SqlClientDelete.Return {
        fun execute(): Mono<Int>
    }
}
