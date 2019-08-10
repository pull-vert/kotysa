package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.Table
import com.pullvert.kotysa.TablesDsl
import kotlin.reflect.KClass

/**
 * @sample com.pullvert.kotysa.sample.sqlitetables
 * @author Fred Montariol
 */
class SqLiteTablesDsl(init: SqLiteTablesDsl.() -> Unit) : TablesDsl<SqLiteTablesDsl, SqLiteTableDsl<*>>(init) {

    override fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: SqLiteTableDsl<*>.() -> Unit): Table<*> {
        val sqLiteTableDsl = SqLiteTableDsl(dsl, tableClass)
        return sqLiteTableDsl.initialize(sqLiteTableDsl)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> table(noinline dsl: SqLiteTableDsl<T>.() -> Unit) {
        table(T::class, dsl as SqLiteTableDsl<*>.() -> Unit)
    }
}
