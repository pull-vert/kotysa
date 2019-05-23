/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class SqlClientDelete private constructor() {
    interface Delete : Return

    interface Return
}

/**
 * @author Fred Montariol
 */
class SqlClientDeleteBlocking private constructor() {
    interface Delete : SqlClientDelete.Delete, Return

    interface Return : SqlClientDelete.Return {
        fun execute(): Int
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientDelete protected constructor() {

    protected interface DeleteProperties<T : Any> {
        val tables: Tables
        val tableClass: KClass<T>
    }

    protected interface Delete<T : Any> : SqlClientDelete.Delete, Return<T> {
        val tables: Tables
        val tableClass: KClass<T>
    }

    protected interface Return<T : Any> : SqlClientDelete.Return {
        val deleteProperties: DeleteProperties<T>

        fun deleteFromTableSql(tableClass: KClass<*>): String {
            val table = deleteProperties.tables.getTable(tableClass)
            val deleteSql = "DELETE FROM ${table.name}"
            logger.debug { "Exec SQL : $deleteSql" }
            return deleteSql
        }
    }
}
