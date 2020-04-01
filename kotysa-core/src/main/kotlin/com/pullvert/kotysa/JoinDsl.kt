/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
@KotysaMarker
public class JoinDsl internal constructor(
        private val init: (FieldProvider) -> ColumnField<*, *>,
        private val table: AliasedTable<*>,
        private val type: JoinType,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        dbType: DbType
) : SimpleFieldProvider(availableColumns, dbType) {

    internal fun initialize() = JoinClause(table, init(this), type)
}
