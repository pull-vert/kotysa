/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
data class WhereClause<T : Any> internal constructor(
        internal val field: Field,
        internal val operation: Operation,
        internal val value: Pair<KClass<T>, T?>?
)

internal enum class Operation {
    EQ, NEQ, SUP, INF, NNULL, NULL
}
