/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.postgresql

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.postgresqlTables
 * @author Fred Montariol
 */
public class PostgresqlTablesDsl(init: PostgresqlTablesDsl.() -> Unit) : TablesDsl<PostgresqlTablesDsl, PostgresqlTableDsl<*>>(init) {

    override fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: PostgresqlTableDsl<*>.() -> Unit): Table<*> {
        val tableDsl = PostgresqlTableDsl(dsl, tableClass)
        return tableDsl.initialize(tableDsl)
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Any> table(noinline dsl: PostgresqlTableDsl<T>.() -> Unit) {
        table(T::class, dsl as PostgresqlTableDsl<*>.() -> Unit)
    }
}
