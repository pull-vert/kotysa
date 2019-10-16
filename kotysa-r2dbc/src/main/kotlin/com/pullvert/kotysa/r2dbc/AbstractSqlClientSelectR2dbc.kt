/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
internal abstract class AbstractSqlClientSelectR2dbc protected constructor() : DefaultSqlClientSelect() {

    protected interface Return<T : Any> : DefaultSqlClientSelect.Return<T> {

        val client: DatabaseClient

        fun fetch() = with(properties) {
            var executeSpec = client.execute(selectSql())

            whereClauses
                    .mapNotNull { whereClause -> whereClause.value }
                    .forEachIndexed { index, value ->
                        executeSpec = executeSpec.bind(index, value)
                    }

            executeSpec.map { r, _ ->
                val row = R2dbcRow(r, selectInformation.fieldIndexMap)
                selectInformation.select(row, row)
            }
        }

        @Suppress("UNCHECKED_CAST")
        private class R2dbcRow(
                private val r2bcRow: Row,
                fieldIndexMap: Map<Field, Int>
        ) : AbstractRow(fieldIndexMap) {
            override fun <T> get(index: Int, type: Class<T>) = r2bcRow.get(index, type) as T
        }
    }
}
