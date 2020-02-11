/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.sqLiteTables
 * @author Fred Montariol
 */
class SqLiteTablesDsl(init: SqLiteTablesDsl.() -> Unit) : TablesDsl<SqLiteTablesDsl, SqLiteTableDsl<*>>(init) {

    override fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: SqLiteTableDsl<*>.() -> Unit): Table<*> {
        val tableDsl = SqLiteTableDsl(dsl, tableClass)
        return tableDsl.initialize(tableDsl)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> table(noinline dsl: SqLiteTableDsl<T>.() -> Unit) {
        table(T::class, dsl as SqLiteTableDsl<*>.() -> Unit)
    }
}
