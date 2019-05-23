/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.DatabaseChoice
import com.pullvert.kotysa.Tables
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
        allColumns.putAll(table.columns)
    }
}

/**
 * Configure Functional Table Mapping support for H2
 * @see H2TablesDsl
 */
fun DatabaseChoice.h2(dsl: H2TablesDsl.() -> Unit): Tables {
    val h2TablesDsl = H2TablesDsl(dsl)
    return h2TablesDsl.initialize(h2TablesDsl)
}
