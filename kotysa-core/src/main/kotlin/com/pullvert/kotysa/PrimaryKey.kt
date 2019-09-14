/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
interface PrimaryKey

/**
 * @author Fred Montariol
 */
internal inline class SinglePrimaryKey<T : Any, U>(internal val column: Column<T, U>): PrimaryKey
