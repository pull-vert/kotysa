/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.postgresql

import com.github.michaelbull.logging.InlineLogger
import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.postgresqlUpdateTableSql(logger: InlineLogger) = with(properties) {
    val updateSql = "UPDATE ${table.name}"
    var index = 1
    val setSql = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = $${index++}" }
    val joinsAndWheres = joinsWithExistsAndWheres(offset = index)
    logger.debug { "Exec SQL (${tables.dbType.name}) : $updateSql $setSql $joinsAndWheres" }

    "$updateSql $setSql $joinsAndWheres"
}
