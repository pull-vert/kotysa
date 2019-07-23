/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * All Mapped Tables
 * @author Fred Montariol
 */
data class Tables internal constructor(
        val allTables: Map<KClass<*>, Table<*>>,
        internal val allColumns: Map<out (Any) -> Any?, Column<*, *>>
)

/**
 * A database Table model mapped by entity class [tableClass]
 *
 * @author Fred Montariol
 */
class Table<T : Any> internal constructor(
        internal val tableClass: KClass<T>,
        /**
         * Real name of this table in the database
         */
        val name: String,
        val columns: Map<(T) -> Any?, Column<T, *>>
)
