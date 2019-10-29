/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
interface ForeignKey

/**
 * @author Fred Montariol
 */
internal data class SingleForeignKey<T : Any, U : Any, V> internal constructor(
		internal val name: String,
		internal val column: Column<T, V>,
		internal var referencedColumn: Column<U, V>
) : ForeignKey
