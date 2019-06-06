/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class SqlClientUpdate private constructor() {
    interface Update<T : Any> : Return {
        fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : Return

    interface Return
}

/**
 * @author Fred Montariol
 */
class SqlClientUpdateBlocking private constructor() {
    interface Update<T : Any> : SqlClientUpdate.Update<T>, Return {
        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientUpdate.Where, Return

    interface Return : SqlClientUpdate.Return {
        fun execute()
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientUpdate protected constructor() : DefaultSqlClientCommon() {

    class Properties<T : Any>(
            override val tables: Tables,
            val table: Table<T>,
            override val whereClauses: MutableList<WhereClause>,
            override val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties

    abstract class Update<T : Any> protected constructor(
            tables: Tables,
            tableClass: KClass<T>
    ) : DefaultSqlClientCommon.Instruction(), SqlClientUpdate.Update<T>, Return<T> {
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

    protected interface Where<T : Any> : TypedWhere<T>, SqlClientUpdate.Where, Return<T>

    protected interface Return<T : Any> : DefaultSqlClientCommon.Return, SqlClientUpdate.Return {
        override val properties: Properties<T>

        fun updateTableSql() = with(properties) {
            val updateSql = "UPDATE ${table.name}"
            val whereAndWhereDebug = whereAndWhereDebug(whereClauses, logger)
            logger.debug { "Exec SQL : $updateSql ${whereAndWhereDebug.second}" }

            "$updateSql ${whereAndWhereDebug.first}"
        }
    }
}
