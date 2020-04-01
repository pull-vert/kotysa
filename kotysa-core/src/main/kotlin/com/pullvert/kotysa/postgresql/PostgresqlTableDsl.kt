/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.postgresql

import com.pullvert.kotysa.ColumnBuilder
import com.pullvert.kotysa.TableColumnPropertyProvider
import com.pullvert.kotysa.TableDsl
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
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
