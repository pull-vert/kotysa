/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
interface PrimaryKey {
    val name: String?
}

/**
 * @author Fred Montariol
 */
internal class SinglePrimaryKey<T : Any, U> internal constructor(
        override val name: String?,
        internal val column: Column<T, U>
): PrimaryKey
