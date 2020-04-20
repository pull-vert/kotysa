/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.ColumnBuilder
import com.pullvert.kotysa.TableColumnPropertyProvider
import com.pullvert.kotysa.TableDsl
import kotlin.reflect.KClass


public class SqLiteTableDsl<T : Any>(
        init: SqLiteTableDsl<T>.() -> Unit,
        tableClass: KClass<T>
) : TableDsl<T, SqLiteTableDsl<T>>(init, tableClass) {

    /**
     * Declare a Column, supported types follow : [SqLite Data types](https://www.sqlite.org/datatype3.html)
     */
    public fun column(dsl: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>) {
        val columnDsl = SqLiteColumnDsl(dsl)
        val column = columnDsl.initialize(columnDsl)
        addColumn(column)
    }
}
