/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientUpdateR2dbc private constructor() : DefaultSqlClientUpdate() {

    internal class Update<T : Any> internal constructor(
            override val client: DatabaseClient,
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientUpdate.Update<T>(tables, tableClass), ReactorSqlClientUpdate.Update<T>, Return<T> {

        override fun set(dsl: (FieldSetter<T>) -> Unit): ReactorSqlClientUpdate.Update<T> {
            addSetValue(dsl)
            return this
        }

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): ReactorSqlClientUpdate.Where {
            val where = Where(client, properties)
            where.addWhereClause(dsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientUpdate.Where<T>, ReactorSqlClientUpdate.Where, Return<T>

    private interface Return<T : Any> : DefaultSqlClientUpdate.Return<T>, ReactorSqlClientUpdate.Return {
        val client: DatabaseClient

        override fun execute() = with(properties) {
            require(setValues.isNotEmpty()) { "At least one value must be set in Update" }

            var executeSpec = client.execute()
                    .sql(updateTableSql())

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

            executeSpec.fetch().rowsUpdated()
        }
    }
}
