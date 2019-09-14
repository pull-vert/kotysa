/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
class WhereClause internal constructor(
        internal val field: Field,
        internal val operation: Operation,
        val value: Any?
)

internal enum class Operation {
    EQ, NEQ, SUP, INF
}
