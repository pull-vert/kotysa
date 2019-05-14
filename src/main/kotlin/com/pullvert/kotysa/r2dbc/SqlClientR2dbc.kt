/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import com.pullvert.kotysa.DefaultSqlClient
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class SqlClientR2dbc(
        private val client: DatabaseClient,
        override val tables: Tables
) : DefaultSqlClient, ReactorSqlClient {

    override fun <T : Any> select(resultClass: KClass<T>, selectDsl: ((ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T> {
        selectCheck(resultClass, selectDsl)
        return SqlClientSelectR2dbc.Select(client, tables, resultClass, selectDsl)
    }

    override fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void> {
        val createTableSql = createTableSql(tableClass)
        return client.execute().sql(createTableSql).then()
    }

    override fun createTables(vararg tableClasses: KClass<*>): Mono<Void> =
            if (tableClasses.isEmpty()) {
                createTables(*tables.allTables.keys.toTypedArray())
            } else {
                // fail-fast : check that all tables are mapped Tables
                tables.checkTables(tableClasses)

                tableClasses.toFlux()
                        .flatMap { tableClass -> createTable(tableClass) }
                        .then()
            }

    override fun <T : Any> insert(row: T): Mono<Void> {
        val insertSql = insertSql(row)
        return client.execute().sql(insertSql).then()
    }

    override fun insert(vararg rows: Any): Mono<Void> {
        // fail-fast : check that all tables are mapped Tables
        rows.forEach { row -> tables.checkTable(row::class) }

        return rows.toFlux()
                .flatMap { tableClass -> insert(tableClass) }
                .then()
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): ReactorSqlClientDelete.Delete =
            SqlClientDeleteR2dbc.Delete(client, tables, tableClass)
}

fun DatabaseClient.sqlClient(tables: Tables): ReactorSqlClient = SqlClientR2dbc(this, tables)
