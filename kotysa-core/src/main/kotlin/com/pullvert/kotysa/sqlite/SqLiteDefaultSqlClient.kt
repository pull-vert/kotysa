/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate
import mu.KLogger

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.sqLiteDeleteFromTableSql(logger: KLogger) = with(properties) {
    val joinsAndWheres = joinsWithExistsAndWheres(false)
    logger.debug {
        val joinsAndWheresDebug = if (joinsAndWheres.isNotEmpty()) {
            "WHERE $joinsAndWheres"
        } else {
            ""
        }
        "Exec SQL (SqLite) : DELETE FROM ${table.name} $joinsAndWheresDebug"
    }
    joinsAndWheres
}

/**
 * @author Fred Montariol
 */
internal fun DefaultSqlClientDeleteOrUpdate.Return<*>.sqLiteUpdateTableSql(logger: KLogger) = with(properties) {
    val joinsAndWheres = joinsWithExistsAndWheres(false)
    logger.debug {
        val setSqlDebug = setValues.keys.joinToString(prefix = "SET ") { column -> "${column.name} = ?" }
        val joinsAndWheresDebug = if (joinsAndWheres.isNotEmpty()) {
            "WHERE $joinsAndWheres"
        } else {
            ""
        }
        "Exec SQL (SqLite) : UPDATE ${table.name} $setSqlDebug $joinsAndWheresDebug"
    }
    joinsAndWheres
}
