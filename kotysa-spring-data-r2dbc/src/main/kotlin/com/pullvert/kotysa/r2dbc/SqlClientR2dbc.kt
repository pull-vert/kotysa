/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.SelectDslApi
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.ValueProvider
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import kotlin.reflect.KClass

/**
 * see [spring-data-r2dbc doc](https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference)
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
internal class SqlClientR2dbc(
        override val client: DatabaseClient,
        override val tables: Tables
) : ReactorSqlClient(), AbstractSqlClientR2dbc {

    override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T> =
            SqlClientSelectR2dbc.Select(client, tables, resultClass, dsl)

    override fun <T : Any> createTable(tableClass: KClass<T>) =
            executeCreateTable(tableClass).then()

    override fun <T : Any> insert(row: T) =
            executeInsert(row).then()

    override fun insert(vararg rows: Any): Mono<Void> {
        checkRowsAreMapped(*rows)

        return rows.toFlux()
                .flatMap { row -> insert(row) }
                .then()
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): ReactorSqlClientDeleteOrUpdate.DeleteOrUpdate<T> =
            SqlClientDeleteR2dbc.Delete(client, tables, tableClass)

    override fun <T : Any> updateTable(tableClass: KClass<T>): ReactorSqlClientDeleteOrUpdate.Update<T> =
            SqlClientUpdateR2dbc.Update(client, tables, tableClass)
}

/**
 * Create a [ReactorSqlClient] from a R2DBC [DatabaseClient] with [Tables] mapping
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
fun DatabaseClient.sqlClient(tables: Tables): ReactorSqlClient = SqlClientR2dbc(this, tables)
