/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.FetchSpec

/**
 * @author Fred Montariol
 */
internal abstract class AbstractSqlClientDeleteR2dbc protected constructor() : DefaultSqlClientDeleteOrUpdate() {

    protected interface Return<T : Any> : DefaultSqlClientDeleteOrUpdate.Return<T> {
        val client: DatabaseClient

        fun fetch(): FetchSpec<Map<String, Any>> = with(properties) {
            var executeSpec = client.execute(deleteFromTableSql())

            whereClauses
                    .mapNotNull { typedWhereClause -> typedWhereClause.whereClause.value }
                    .forEachIndexed { index, value ->
                        executeSpec = executeSpec.bind(index, value)
                    }

            executeSpec.fetch()
        }
    }
}
