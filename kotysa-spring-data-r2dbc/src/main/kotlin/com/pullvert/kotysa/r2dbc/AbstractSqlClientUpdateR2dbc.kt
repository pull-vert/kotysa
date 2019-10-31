/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.bind
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal abstract class AbstractSqlClientUpdateR2dbc protected constructor() : DefaultSqlClientDeleteOrUpdate() {

    protected interface Return<T : Any> : DefaultSqlClientDeleteOrUpdate.Return<T> {
        val client: DatabaseClient

        fun fetch() = with(properties) {
            require(setValues.isNotEmpty()) { "At least one value must be set in Update" }

            var executeSpec = client.execute(updateTableSql())

            var index = 0
            setValues.forEach { (column, value) ->
                executeSpec = if (value == null) {
                    executeSpec.bindNull(index, (column.entityGetter.toCallable().returnType.classifier as KClass<*>).java)
                } else {
                    executeSpec.bind(index, value)
                }
                index++
            }

            whereClauses
                    .mapNotNull { whereClause -> whereClause.value }
                    .forEach { value ->
                        executeSpec = executeSpec.bind(index, value)
                        index++
                    }

            executeSpec.fetch()
        }
    }
}
