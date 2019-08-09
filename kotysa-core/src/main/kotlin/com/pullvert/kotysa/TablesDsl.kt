/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 *
 * @sample com.pullvert.kotysa.sample.h2tables
 */
@KotysaMarker
abstract class TablesDsl<T : TablesDsl<T>>(private val init: T.() -> Unit) {

    @PublishedApi
    internal val tables = mutableMapOf<KClass<*>, Table<*>>()
    @PublishedApi
    internal val allColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()

    internal fun initialize(initialize: T): Tables {
        init(initialize)
        require(tables.isNotEmpty()) { "Tables must declare at least one table" }
        return Tables(tables, allColumns)
    }
}
