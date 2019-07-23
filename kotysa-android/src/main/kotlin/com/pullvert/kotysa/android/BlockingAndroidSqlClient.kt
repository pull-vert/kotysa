/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * Android Sql Client
 *
 * @author Fred Montariol
 */
abstract class BlockingAndroidSqlClient : BlockingSqlClient {
    @PublishedApi
    internal abstract fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): BlockingSqlClientSelect.Select<T>

    @PublishedApi
    internal abstract fun <T : Any> createTable(tableClass: KClass<T>)

    @PublishedApi
    internal abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): BlockingSqlClientDelete.Delete<T>

    @PublishedApi
    internal abstract fun <T : Any> updateTable(tableClass: KClass<T>): BlockingSqlClientUpdate.Update<T>
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.select(noinline dsl: SelectDslApi.(ValueProvider) -> T) = select(T::class, dsl)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.select() = select(T::class, null)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.selectAll() = select(T::class, null).fetchAll()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.countAll() = select(kotlin.Long::class) { count<T>() }.fetchOne()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.createTable() = createTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.deleteFromTable() = deleteFromTable(T::class)

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.deleteAllFromTable() = deleteFromTable(T::class).execute()

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> BlockingAndroidSqlClient.updateTable() = updateTable(T::class)
