/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.github.michaelbull.logging.InlineLogger
import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.h2DeleteFromTableSql(logger: InlineLogger) = with(properties) {
    val deleteSql = "DELETE FROM ${table.name}"
    val joinsAndWheres = joinsWithExistsAndWheres()
    logger.debug { "Exec SQL (${tables.dbType.name}) : $deleteSql $joinsAndWheres" }

    "$deleteSql $joinsAndWheres"
}

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.h2UpdateTableSql(logger: InlineLogger) = with(properties) {
    val updateSql = "UPDATE ${table.name}"
    val setSql = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = ?" }
    val joinsAndWheres = joinsWithExistsAndWheres()
    logger.debug { "Exec SQL (${tables.dbType.name}) : $updateSql $setSql $joinsAndWheres" }

    "$updateSql $setSql $joinsAndWheres"
}
