/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.postgresql

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.postgresqlTables
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
