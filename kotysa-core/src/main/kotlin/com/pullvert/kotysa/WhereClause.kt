/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
public class WhereClause internal constructor(
        internal val field: Field,
        internal val operation: Operation,
        public val value: Any?
)

internal enum class Operation {
    EQ, NOT_EQ, CONTAINS, STARTS_WITH, ENDS_WITH, SUP, INF, SUP_OR_EQ, INF_OR_EQ, IS
}

public class TypedWhereClause internal constructor(
        public val whereClause: WhereClause,
        internal val type: WhereClauseType
)

internal enum class WhereClauseType {
    WHERE, AND, OR
}
