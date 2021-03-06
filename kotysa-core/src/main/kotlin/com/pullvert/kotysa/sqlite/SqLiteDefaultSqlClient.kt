/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.sqlite

import com.github.michaelbull.logging.InlineLogger
import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate


internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.sqLiteDeleteFromTableSql(logger: InlineLogger) = with(properties) {
    val joinsAndWheres = joinsWithExistsAndWheres(false)
    logger.debug {
        val joinsAndWheresDebug = if (joinsAndWheres.isNotEmpty()) {
            "WHERE $joinsAndWheres"
        } else {
            ""
        }
        "Exec SQL (${tables.dbType.name}) : DELETE FROM ${table.name} $joinsAndWheresDebug"
    }
    joinsAndWheres
}


internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.sqLiteUpdateTableSql(logger: InlineLogger) = with(properties) {
    val joinsAndWheres = joinsWithExistsAndWheres(false)
    logger.debug {
        val setSqlDebug = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = ?" }
        val joinsAndWheresDebug = if (joinsAndWheres.isNotEmpty()) {
            "WHERE $joinsAndWheres"
        } else {
            ""
        }
        "Exec SQL (${tables.dbType.name}) : UPDATE ${table.name} $setSqlDebug $joinsAndWheresDebug"
    }
    joinsAndWheres
}
