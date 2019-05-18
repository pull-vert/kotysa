/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
data class WhereClause<T : Any, U> internal constructor(
    internal val property: KProperty1<T, U>,
    internal val alias: String?,
    internal val operation: Operation,
    internal val value: Any?
)

internal enum class Operation {
    EQ, NEQ, SUP, INF, NNULL, NULL
}
