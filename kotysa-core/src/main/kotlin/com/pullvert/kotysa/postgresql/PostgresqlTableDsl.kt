/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.postgresql

import com.pullvert.kotysa.ColumnBuilder
import com.pullvert.kotysa.TableColumnPropertyProvider
import com.pullvert.kotysa.TableDsl
import kotlin.reflect.KClass


public class PostgresqlTableDsl<T : Any>(
        init: PostgresqlTableDsl<T>.() -> Unit,
        tableClass: KClass<T>
) : TableDsl<T, PostgresqlTableDsl<T>>(init, tableClass) {

    /**
     * Declare a Column, supported types follow : [Postgres Data types](https://www.postgresql.org/docs/11/datatype.html)
     */
    public fun column(dsl: PostgresqlColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>) {
        val columnDsl = PostgresqlColumnDsl(dsl)
        val column = columnDsl.initialize(columnDsl)
        addColumn(column)
    }
}
