/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.DefaultSqlClient
import com.pullvert.kotysa.getTable
import com.pullvert.kotysa.toCallable
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * see [spring-data-r2dbc doc](https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference)
 * @author Fred Montariol
 */
internal interface AbstractSqlClientR2dbc : DefaultSqlClient {

    val client: DatabaseClient

    fun <T : Any> executeCreateTable(tableClass: KClass<T>) =
            client.execute(createTableSql(tableClass))

    fun <T : Any> executeInsert(row: T): DatabaseClient.GenericExecuteSpec {
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
