/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.DatabaseChoice
import com.pullvert.kotysa.TableDsl
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.TablesDsl

/**
 * @author Fred Montariol
 */
class H2TablesDsl(init: H2TablesDsl.() -> Unit) : TablesDsl<H2TablesDsl>(init) {
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
 * Configure Functional Table Mapping support for H2
 * @see H2TablesDsl
 */
fun DatabaseChoice.h2(dsl: H2TablesDsl.() -> Unit): Tables {
    val h2Tables = H2TablesDsl(dsl)
    return h2Tables.initialize(h2Tables)
}
