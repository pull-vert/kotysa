/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.postgresql

import com.github.michaelbull.logging.InlineLogger
import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate


internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.postgresqlUpdateTableSql(logger: InlineLogger) = with(properties) {
    val updateSql = "UPDATE ${table.name}"
    var index = 1
    val setSql = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = $${index++}" }
    val joinsAndWheres = joinsWithExistsAndWheres(offset = index)
    logger.debug { "Exec SQL (${tables.dbType.name}) : $updateSql $setSql $joinsAndWheres" }

    "$updateSql $setSql $joinsAndWheres"
}
