/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
@KotysaMarker
class TablesDsl(private val init: TablesDsl.() -> Unit) {

    @PublishedApi
    internal val tables = mutableMapOf<KClass<*>, Table<*>>()
    @PublishedApi
    internal val allColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()

    inline fun <reified T : Any> table(noinline dsl: TableDsl<T>.() -> Unit) {
        val tableClass = T::class
        if (tables.containsKey(tableClass)) {
            throw IllegalStateException("Trying to map entity class \"${tableClass.qualifiedName}\" to multiple tables")
        }
        val tableDsl = TableDsl(dsl, tableClass)
        val table = tableDsl.initialize()
        tables[tableClass] = table
        @Suppress("UNCHECKED_CAST")
        allColumns.putAll(table.columns as Map<out (Any) -> Any?, Column<*, *>>)
    }

    internal fun initialize(): Tables {
        init(this)
        require(tables.isNotEmpty()) { "Tables must declare at least one table" }
        return Tables(tables, allColumns)
    }
}

/**
 * Configure Functional Table Mapping support for H2
 *
 * @see TablesDsl
 * @author Fred Montariol
 */
fun tables(dsl: TablesDsl.() -> Unit) = TablesDsl(dsl).initialize()
