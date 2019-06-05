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
internal class SqlClientDeleteR2dbc private constructor() : DefaultSqlClientDelete() {

    internal class Delete<T : Any> internal constructor(
            override val client: DatabaseClient,
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientDelete.Delete<T>(tables, tableClass), ReactorSqlClientDelete.Delete, Return<T> {

        override fun where(whereDsl: WhereDsl.(FieldProvider) -> WhereClause): ReactorSqlClientDelete.Where {
            val where = Where(client, properties)
            where.addWhereClause(whereDsl)
            return where
        }
    }

    private class Where<T : Any> internal constructor(
            override val client: DatabaseClient,
            override val properties: Properties<T>
    ) : DefaultSqlClientDelete.Where<T>, ReactorSqlClientDelete.Where, Return<T>

    private interface Return<T : Any> : DefaultSqlClientDelete.Return<T>, ReactorSqlClientDelete.Return {
        val client: DatabaseClient

        override fun execute() = with(properties) {
            var executeSpec = client.execute()
                    .sql(deleteFromTableSql())

            whereClauses
                    .mapNotNull { whereClause -> whereClause.value }
                    .forEachIndexed { index, value ->
                        executeSpec = executeSpec.bind(index, value)
                    }

            executeSpec.fetch().rowsUpdated()
        }
    }
}
