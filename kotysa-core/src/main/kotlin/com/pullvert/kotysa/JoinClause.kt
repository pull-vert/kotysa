/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
class JoinClause internal constructor(
        internal val table: AliasedTable<*>,
        internal val field: ColumnField<*, *>,
        internal val type: JoinType
)

enum class JoinType(val sql: String) {
    INNER("INNER JOIN"),
    LEFT_OUTER("LEFT OUTER JOIN"),
    RIGHT_OUTER("RIGHT OUTER JOIN"),
    FULL_OUTER("OUTER JOIN"),
    CROSS("CROSSS JOIN")
}
