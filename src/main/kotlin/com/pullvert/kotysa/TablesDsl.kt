/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@KotysaMarker
class TablesDsl(private val init: TablesDsl.() -> Unit) {

	@PublishedApi
	internal val tables = mutableMapOf<KClass<*>, Table<*>>()
	@PublishedApi
	internal val allColumns = mutableMapOf<KProperty1<*, *>, Column<*, *>>()

	fun initialize(): Tables {
		init()
		require(tables.isNotEmpty()) { "Tables must declare at least one table" }
		return Tables(tables, allColumns)
	}

	inline fun <reified T : Any> table(noinline dsl: TableDsl<T>.() -> Unit) {
		val tableClass = T::class
		if (tables.containsKey(tableClass)) {
			throw IllegalStateException("Trying to map entity class \"${tableClass.qualifiedName}\" to multiple tables")
		}
		val table = TableDsl(dsl, tableClass).initialize()
		tables[tableClass] = table
		allColumns.putAll(table.columns)
	}
}

/**
 * Configure Functional Table Mapping support.
 * @see TablesDsl
 */
fun tables(dsl: TablesDsl.() -> Unit) = TablesDsl(dsl).initialize()
