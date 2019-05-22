/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class TablesDsl<T : TablesDsl<T>>(private val init: T.() -> Unit) {

    @PublishedApi
    internal val tables = mutableMapOf<KClass<*>, Table<*>>()
    @PublishedApi
    internal val allColumns = mutableMapOf<KProperty1<*, *>, Column<*, *>>()

    internal fun initialize(initialize: T): Tables {
        init(initialize)
        require(tables.isNotEmpty()) { "Tables must declare at least one table" }
        return Tables(tables, allColumns)
    }
}

/**
 * Supported Database Choice (via extension functions)
 */
object DatabaseChoice

/**
 * Choose the database's Type
 *
 * @see TablesDsl
 * @see DatabaseChoice
 */
fun tables() = DatabaseChoice
