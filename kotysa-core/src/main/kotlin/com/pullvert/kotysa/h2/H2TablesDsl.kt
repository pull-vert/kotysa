/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.h2Tables
 */
public class H2TablesDsl(init: H2TablesDsl.() -> Unit) : TablesDsl<H2TablesDsl, H2TableDsl<*>>(init) {

    override fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: H2TableDsl<*>.() -> Unit): Table<*> {
        val tableDsl = H2TableDsl(dsl, tableClass)
        return tableDsl.initialize(tableDsl)
    }

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Any> table(noinline dsl: H2TableDsl<T>.() -> Unit) {
        table(T::class, dsl as H2TableDsl<*>.() -> Unit)
    }
}
