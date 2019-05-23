/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.ColumnBuilder
import com.pullvert.kotysa.TableColumnPropertyProvider
import com.pullvert.kotysa.TableDsl
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class H2TableDsl<T : Any>(
        init: H2TableDsl<T>.() -> Unit,
        tableClass: KClass<T>
) : TableDsl<T, H2TableDsl<T>>(init, tableClass) {

    fun column(dsl: H2ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>) {
        val h2ColumnDsl = H2ColumnDsl(dsl)
        val column = h2ColumnDsl.initialize(h2ColumnDsl)
        addColumn(column)
    }
}
