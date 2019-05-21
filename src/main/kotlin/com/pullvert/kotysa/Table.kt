/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * All Mapped Tables
 * @author Fred Montariol
 */
data class Tables internal constructor(
        internal val allTables: Map<KClass<*>, Table<*>>,
        internal val allColumns: Map<KProperty1<*, *>, Column<*, *>>
)

/**
 * One database Table model mapped by entity class [tableClass]
 * @author Fred Montariol
 */
data class Table<T : Any> internal constructor(
        internal val tableClass: KClass<T>,
        internal val name: String,
        @PublishedApi internal val columns: Map<KProperty1<T, *>, Column<T, *>>
)
