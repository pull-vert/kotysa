/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Database all Mapped Tables' model
 * @author Fred Montariol
 */
data class Tables(
		val allTables: Map<KClass<*>, Table<*>>,
		val allColumns: Map<KProperty1<*, *>, Column<*, *>>
)

/**
 * One database Table model mapped by entity class [tableClass]
 * @author Fred Montariol
 */
data class Table<T : Any>(
		val tableClass: KClass<T>,
		val name: String,
		val columns: Map<KProperty1<T, *>, Column<T, *>>
)
