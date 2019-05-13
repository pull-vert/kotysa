/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.AbstractSqlClient
import com.pullvert.kotysa.SqlClientSelect
import com.pullvert.kotysa.ValueProvider
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * Reactive (using Reactor) Sql Client, to be used with any non-blocking driver, such as R2dbc
 * @author Fred Montariol
 */
interface ReactorSqlClient : AbstractSqlClient {

    override fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T>

    override fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void>

    override fun createTables(vararg tableClasses: KClass<*>): Mono<Void>

    override fun <T : Any> insert(row: T): Mono<Void>

    override fun insert(vararg rows: Any): Mono<Void>
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
class ReactorSqlClientSelect private constructor() {
    interface Select<T : Any> : SqlClientSelect.AbstractSelect<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.AbstractReturn<T> {
        override fun fetchOne(): Mono<T>
        override fun fetchAll(): Flux<T>
    }
}
