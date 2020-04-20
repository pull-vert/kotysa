/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.sqLiteTables
 */
public class SqLiteTablesDsl(init: SqLiteTablesDsl.() -> Unit) : TablesDsl<SqLiteTablesDsl, SqLiteTableDsl<*>>(init) {

    override fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: SqLiteTableDsl<*>.() -> Unit): Table<*> {
        val tableDsl = SqLiteTableDsl(dsl, tableClass)
        return tableDsl.initialize(tableDsl)
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Any> table(noinline dsl: SqLiteTableDsl<T>.() -> Unit) {
        table(T::class, dsl as SqLiteTableDsl<*>.() -> Unit)
    }
}
