/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * see [spring-data-r2dbc doc](https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference)
 * @author Fred Montariol
 */
internal abstract class AbstractSqlClientR2dbc(
        private val client: DatabaseClient,
        override val tables: Tables
) : ReactorSqlClient(), DefaultSqlClient {

    protected fun <T : Any> executeCreateTable(tableClass: KClass<T>) =
            client.execute(createTableSql(tableClass))

    protected fun <T : Any> executeInsert(row: T): DatabaseClient.GenericExecuteSpec {
        var executeSpec = client.execute(insertSql(row))
        val table = tables.getTable(row::class)
        table.columns.values.forEachIndexed { index, column ->
            val value = column.entityGetter(row)
            executeSpec = if (value == null) {
                executeSpec.bindNull(index, (column.entityGetter.toCallable().returnType.classifier as KClass<*>).java)
            } else {
                executeSpec.bind(index, value)
            }
        }
        return executeSpec
    }
}
