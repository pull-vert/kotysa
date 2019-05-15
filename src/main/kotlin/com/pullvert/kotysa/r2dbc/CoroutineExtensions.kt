/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.flow.asFlow
import kotlin.reflect.KClass

/**
 * Non-nullable Coroutines variant of [ReactorSqlClientSelect.Return.fetchOne].
 *
 * @author Fred Montariol
 */
suspend fun <T : Any> ReactorSqlClientSelect.Return<T>.fetchAwaitOne(): T = fetchOne().awaitSingle()

/**
 * Nullable Coroutines variant of [ReactorSqlClientSelect.Return.fetchOne].
 *
 * @author Fred Montariol
 */
suspend fun <T : Any> ReactorSqlClientSelect.Return<T>.fetchAwaitOneOrNull(): T? = fetchOne().awaitFirstOrNull()

/**
 * Non-nullable Coroutines variant of [ReactorSqlClientSelect.Return.fetchFirst].
 *
 * @author Fred Montariol
 */
suspend fun <T : Any> ReactorSqlClientSelect.Return<T>.fetchAwaitFirst(): T = fetchFirst().awaitSingle()

/**
 * Nullable Coroutines variant of [ReactorSqlClientSelect.Return.fetchFirst].
 *
 * @author Fred Montariol
 */
suspend fun <T : Any> ReactorSqlClientSelect.Return<T>.fetchAwaitFirstOrNull(): T? = fetchFirst().awaitFirstOrNull()

/**
 * Coroutines [Flow] variant of [ReactorSqlClientSelect.Return.fetchAll].
 *
 * Backpressure is controlled by [batchSize] parameter that controls the size of in-flight elements
 * and [org.reactivestreams.Subscription.request] size.
 *
 * @author Fred Montariol
 */
@FlowPreview
fun <T: Any> ReactorSqlClientSelect.Return<T>.fetchFlow(batchSize: Int = 1): Flow<T> = fetchAll().asFlow(batchSize)

/**
 * Coroutines variant of [ReactorSqlClientDelete.Return.execute].
 *
 * @author Fred Montariol
 */
suspend fun ReactorSqlClientDelete.Return.awaitExecute(): Int = execute().awaitSingle()

suspend fun <T : Any> ReactorSqlClient.awaitInsert(row: T) {
    insert(row).awaitFirstOrNull()
}

suspend fun ReactorSqlClient.awaitInsert(vararg rows: Any) {
    insert(*rows).awaitFirstOrNull()
}

suspend inline fun <reified T : Any> ReactorSqlClient.awaitCreateTable() {
    createTable(T::class).awaitFirstOrNull()
}

suspend fun ReactorSqlClient.awaitCreateTables(vararg tableClasses: KClass<*>) {
    createTables(*tableClasses).awaitFirstOrNull()
}
