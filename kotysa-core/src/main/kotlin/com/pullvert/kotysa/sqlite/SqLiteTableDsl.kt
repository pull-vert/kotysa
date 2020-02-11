/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.ColumnBuilder
import com.pullvert.kotysa.TableColumnPropertyProvider
import com.pullvert.kotysa.TableDsl
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class SqLiteTableDsl<T : Any>(
        init: SqLiteTableDsl<T>.() -> Unit,
        tableClass: KClass<T>
) : TableDsl<T, SqLiteTableDsl<T>>(init, tableClass) {

    /**
     * Declare a Column, supported types follow : [SqLite Data types](https://www.sqlite.org/datatype3.html)
     */
    fun column(dsl: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>) {
        val columnDsl = SqLiteColumnDsl(dsl)
        val column = columnDsl.initialize(columnDsl)
        addColumn(column)
    }
}
