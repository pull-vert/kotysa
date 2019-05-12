/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import io.r2dbc.spi.Row
import org.apache.commons.logging.LogFactory
import org.springframework.data.r2dbc.core.DatabaseClient
import com.pullvert.kotysa.AbstractRow
import com.pullvert.kotysa.DefaultSqlClientSelect
import com.pullvert.kotysa.SqlClientSelect.ReactiveReturn
import com.pullvert.kotysa.SqlClientSelect.ReactiveSelect
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.ValueProvider
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
internal class SqlClientSelectR2dbc private constructor() {
	internal class SqlClientPropertiesR2dbc<T : Any>(
			val client: DatabaseClient,
			override val tables: Tables,
			override val resultClass: KClass<T>,
			override val transform: ((ValueProvider) -> T)?
	): DefaultSqlClientSelect.SqlClientProperties<T>

	internal class R2dbcSelect<T : Any>(
			private val client: DatabaseClient,
			override val tables: Tables,
			override val resultClass: KClass<T>,
			override val transform: ((ValueProvider) -> T)? = null
	) : DefaultSqlClientSelect.Select<T>, ReactiveSelect<T>, R2dbcReturn<T> {

		override val logger = LogFactory.getLog(R2dbcSelect::class.java)

		override val sqlClientProperties: SqlClientPropertiesR2dbc<T>
			get() {
				return SqlClientPropertiesR2dbc(client, tables, resultClass, transform)
			}
	}

	internal interface R2dbcReturn<T : Any> : DefaultSqlClientSelect.Return<T>, ReactiveReturn<T> {
		override val sqlClientProperties: SqlClientPropertiesR2dbc<T>

		override fun fetchOne() = fetch().one()
		override fun fetchAll() = fetch().all()

		private fun fetch() = with(sqlClientProperties) {
			val selectInformation = getSelectInformation()
			client.execute()
					.sql(selectSql(selectInformation))
					.map { r,_ ->
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
