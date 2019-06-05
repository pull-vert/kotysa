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
    interface Delete : Return {
        fun where(whereDsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    interface Where : Return

    interface Return
}

/**
 * @author Fred Montariol
 */
class SqlClientDeleteBlocking private constructor() {
    interface Delete : SqlClientDelete.Delete, Return {
        override fun where(whereDsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    interface Where : SqlClientDelete.Where, Return

    interface Return : SqlClientDelete.Return {
        fun execute(): Int
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientDelete protected constructor() : DefaultSqlClientCommon() {

    class Properties<T : Any>(
            override val tables: Tables,
            val table: Table<T>,
            override val whereClauses: MutableList<WhereClause>,
            override val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties

    abstract class Delete<T : Any> protected constructor(
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientCommon.Instruction(), SqlClientDelete.Delete, Return<T> {
        final override val properties: Properties<T>

        init {
            tables.checkTable(tableClass)
            val table = tables.getTable(tableClass)
            // build availableColumns Map
            val availableColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()
            addAvailableColumnsFromTable(availableColumns, table)
            properties = Properties(tables, table, mutableListOf(), availableColumns)
        }
    }

    protected interface Where<T : Any> : DefaultSqlClientCommon.Where, SqlClientDelete.Where, Return<T>

    protected interface Return<T : Any> : DefaultSqlClientCommon.Return, SqlClientDelete.Return {
        override val properties: Properties<T>

        fun deleteFromTableSql() = with(properties) {
            val deleteSql = "DELETE FROM ${table.name}"
            val whereAndWhereDebug = whereAndWhereDebug(whereClauses, logger)
            logger.debug { "Exec SQL : $deleteSql ${whereAndWhereDebug.second}" }

            "$deleteSql ${whereAndWhereDebug.first}"
        }
    }
}
