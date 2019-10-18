/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
private class CoroutinesSqlClientR2Dbc internal constructor(
		override val client: DatabaseClient,
		override val tables: Tables
) : CoroutinesSqlClient(), AbstractSqlClientR2dbc {

	@ExperimentalStdlibApi
	override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T> =
			CoroutinesSqlClientSelectR2dbc.Select(client, tables, resultClass, dsl)

	override suspend fun <T : Any> createTable(tableClass: KClass<T>) =
			executeCreateTable(tableClass).await()

	override suspend fun <T : Any> insert(row: T) =
			executeInsert(row).await()

	override suspend fun insert(vararg rows: Any) {
		checkRowsAreMapped(*rows)

		rows.forEach { row -> insert(row) }
	}

	override fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T> =
			CoroutinesSqlClientDeleteR2dbc.Delete(client, tables, tableClass)

	override fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
			CoroutinesSqlClientUpdateR2dbc.Update(client, tables, tableClass)
}

/**
 * Create a [CoroutinesSqlClient] from a R2DBC [DatabaseClient] with [Tables] mapping
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbcCoroutines
 * @author Fred Montariol
 */
fun DatabaseClient.coSqlClient(tables: Tables): CoroutinesSqlClient = CoroutinesSqlClientR2Dbc(this, tables)
