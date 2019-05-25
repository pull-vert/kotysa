/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.Column
import com.pullvert.kotysa.TablesDsl

/**
 * @author Fred Montariol
 */
class H2TablesDsl(init: H2TablesDsl.() -> Unit) : TablesDsl<H2TablesDsl>(init) {

    inline fun <reified T : Any> table(noinline dsl: H2TableDsl<T>.() -> Unit) {
        val tableClass = T::class
        if (tables.containsKey(tableClass)) {
            throw IllegalStateException("Trying to map entity class \"${tableClass.qualifiedName}\" to multiple tables")
        }
        val h2TableDsl = H2TableDsl(dsl, tableClass)
        val table = h2TableDsl.initialize(h2TableDsl)
        tables[tableClass] = table
        @Suppress("UNCHECKED_CAST")
        allColumns.putAll(table.columns as Map<out (Any) -> Any?, Column<*, *>>)
    }
}
