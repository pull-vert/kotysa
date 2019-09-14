/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate
import mu.KLogger

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.h2DeleteFromTableSql(logger: KLogger) = with(properties) {
    val deleteSql = "DELETE FROM ${table.name}"
    val joinsAndWheres = joinsWithExistsAndWheres()
    logger.debug { "Exec SQL (H2) : $deleteSql $joinsAndWheres" }

    "$deleteSql $joinsAndWheres"
}

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.h2UpdateTableSql(logger: KLogger) = with(properties) {
    val updateSql = "UPDATE ${table.name}"
    val setSql = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = ?" }
    val joinsAndWheres = joinsWithExistsAndWheres()
    logger.debug { "Exec SQL (H2) : $updateSql $setSql $joinsAndWheres" }

    "$updateSql $setSql $joinsAndWheres"
}
