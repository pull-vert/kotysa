/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.AbstractRow
import com.pullvert.kotysa.DefaultSqlClientSelect
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.ValueProvider
import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
internal class SqlClientSelectR2dbc private constructor() {
    internal class SelectPropertiesR2Dbc<T : Any>(
            val client: DatabaseClient,
            override val tables: Tables,
            override val resultClass: KClass<T>,
            override val transform: ((ValueProvider) -> T)?
    ) : DefaultSqlClientSelect.SelectProperties<T>

    internal class R2dbcSelect<T : Any>(
            private val client: DatabaseClient,
            override val tables: Tables,
            override val resultClass: KClass<T>,
            override val transform: ((ValueProvider) -> T)? = null
    ) : DefaultSqlClientSelect.Select<T>, ReactorSqlClientSelect.Select<T>, R2dbcReturn<T> {

        override val selectProperties: SelectPropertiesR2Dbc<T>
            get() {
                return SelectPropertiesR2Dbc(client, tables, resultClass, transform)
            }
    }

    internal interface R2dbcReturn<T : Any> : DefaultSqlClientSelect.Return<T>, ReactorSqlClientSelect.Return<T> {
        override val selectProperties: SelectPropertiesR2Dbc<T>

        override fun fetchOne() = fetch().one()
        override fun fetchAll() = fetch().all()

        private fun fetch() = with(selectProperties) {
            val selectInformation = getSelectInformation()
            client.execute()
                    .sql(selectSql(selectInformation))
                    .map { r, _ ->
                        selectInformation.select.invoke(R2dbcRow(r, selectInformation.columnPropertyIndexMap))
                    }
        }

        @Suppress("UNCHECKED_CAST")
        private class R2dbcRow(
                private val r2bcRow: Row,
                columnPropertyIndexMap: Map<KProperty1<*, *>, Int>
        ) : AbstractRow(columnPropertyIndexMap) {
            override fun <T> get(index: Int, type: Class<T>) = r2bcRow.get(index, type) as T
        }
    }
}
