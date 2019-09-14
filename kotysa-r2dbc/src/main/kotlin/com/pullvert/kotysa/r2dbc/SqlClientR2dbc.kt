/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
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
        private val client: DatabaseClient,
        override val tables: Tables
) : ReactorSqlClient(), DefaultSqlClient {

    @ExperimentalStdlibApi
    override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T> =
            SqlClientSelectR2dbc.Select(client, tables, resultClass, dsl)

    override fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void> {
        val createTableSql = createTableSql(tableClass)
        return client.execute().sql(createTableSql).then()
    }

    override fun <T : Any> insert(row: T): Mono<Void> {
        var executeSpec = client.execute()
                .sql(insertSql(row))
        val table = tables.getTable(row::class)
        table.columns.values.forEachIndexed { index, column ->
            val value = column.entityGetter(row)
            executeSpec = if (value == null) {
                executeSpec.bindNull(index, (column.entityGetter.toCallable().returnType.classifier as KClass<*>).java)
            } else {
                executeSpec.bind(index, value)
            }
        }
        return executeSpec.then()
    }

    override fun insert(vararg rows: Any): Mono<Void> {
        // fail-fast : check that all tables are mapped Tables
        rows.forEach { row -> tables.checkTable(row::class) }

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
