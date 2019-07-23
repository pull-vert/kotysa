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
        fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>

        fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : Return

    interface Return
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
            override val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>,
            val setValues: MutableMap<Column<T, *>, Any?>
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
            properties = Properties(tables, table, mutableListOf(), availableColumns, mutableMapOf())
        }

        protected fun addSetValue(dsl: (FieldSetter<T>) -> Unit) {
            properties.apply {
                val setValue = UpdateSetDsl(dsl, availableColumns).initialize()
                setValues[setValue.first.column] = setValue.second
            }
        }
    }

    protected interface Where<T : Any> : TypedWhere<T>, SqlClientUpdate.Where, Return<T>

    protected interface Return<T : Any> : DefaultSqlClientCommon.Return, SqlClientUpdate.Return {
        override val properties: Properties<T>

        fun updateTableSql() = with(properties) {
            val updateSql = "UPDATE ${table.name}"

            val whereAndWhereDebug = whereAndWhereDebug(whereClauses, logger)

            val setSql = setValues.keys.joinToString(prefix = "SET ") { column ->
                "${column.name} = ?"
            }

            if (logger.isDebugEnabled) {
                val setSqlDebug = setValues.keys.joinToString(prefix = "SET ") { column ->
                    val columnValue = setValues[column]
                    "${column.name} = ${stringValue(columnValue)}"
                }
                logger.debug("Exec SQL : $updateSql $setSqlDebug ${whereAndWhereDebug.second}")
            }

            "$updateSql $setSql ${whereAndWhereDebug.first}"
        }
    }
}
