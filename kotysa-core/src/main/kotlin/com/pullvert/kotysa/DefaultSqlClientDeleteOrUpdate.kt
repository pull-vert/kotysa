/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import com.pullvert.kotysa.h2.h2DeleteFromTableSql
import com.pullvert.kotysa.h2.h2UpdateTableSql
import com.pullvert.kotysa.sqlite.sqLiteDeleteFromTableSql
import com.pullvert.kotysa.sqlite.sqLiteUpdateTableSql
import mu.KotlinLogging
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientDeleteOrUpdate protected constructor() : DefaultSqlClientCommon() {

    class Properties<T : Any> internal constructor(
            override val tables: Tables,
            /**
             * targeted table to update
             */
            val table: Table<T>,
            override val availableColumns: MutableMap<(Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties {
        override val whereClauses: MutableList<WhereClause> = mutableListOf()
        override val joinClauses: MutableList<JoinClause> = mutableListOf()
        val setValues: MutableMap<Column<T, *>, Any?> = mutableMapOf()
    }

    interface WithProperties<T : Any> {
        val properties: Properties<T>
    }

    protected interface DeleteOrUpdate<T : Any> : Instruction {

        val tables: Tables
        val tableClass: KClass<T>

        fun initProperties(): Properties<T> {
            tables.checkTable(tableClass)
            val table = tables.getTable(tableClass)
            val properties = Properties(tables, table, mutableMapOf())
            // init availableColumns with table columns
            addAvailableColumnsFromTable(properties, table)
            return properties
        }
    }

    protected interface Update<T : Any> : DeleteOrUpdate<T>, WithProperties<T> {
        fun addSetValue(dsl: (FieldSetter<T>) -> Unit) {
            properties.apply {
                val setValue = UpdateSetDsl(dsl, availableColumns).initialize()
                setValues[setValue.first.column] = setValue.second
            }
        }
    }

    protected interface Join<T : Any> : DefaultSqlClientCommon.Join, WithProperties<T>, Instruction

    protected interface Where<T : Any> : DefaultSqlClientCommon.Where, WithProperties<T>

    protected interface TypedWhere<T : Any> : DefaultSqlClientCommon.TypedWhere<T>, WithProperties<T>

    interface Return<T : Any> : DefaultSqlClientCommon.Return, WithProperties<T> {

        fun deleteFromTableSql() = when (properties.tables.dbType) {
            DbType.H2 -> h2DeleteFromTableSql(logger)
            DbType.SQLITE -> sqLiteDeleteFromTableSql(logger)
        }

        fun updateTableSql() = when (properties.tables.dbType) {
            DbType.H2 -> h2UpdateTableSql(logger)
            DbType.SQLITE -> sqLiteUpdateTableSql(logger)
        }

        /**
         * Handle joins as EXISTS + nested SELECT
         * Then other WHERE clauses
         */
        fun joinsWithExistsAndWheres(withWhere: Boolean = true) = with(properties) {
            val joins = joinsWithExists()

            var wheres = wheres(false)

            if (joins.isEmpty() && wheres.isEmpty()) {
                ""
            } else {
                val prefix = if (withWhere) {
                    "WHERE "
                } else {
                    ""
                }
                if (joins.isNotEmpty()) {
                    if (wheres.isNotEmpty()) {
                        wheres = "AND $wheres"
                    }
                    "$prefix$joins $wheres )"
                } else {
                    "$prefix$wheres"
                }
            }
        }

        /**
         * Handle joins as EXISTS + nested SELECT
         */
        private fun joinsWithExists() = with(properties) {
            val rootJoinClause = rootJoinClause()
            if (rootJoinClause != null) {
                val joinedTableFieldName = "${rootJoinClause.table.prefix}.${(rootJoinClause.table.primaryKey as SinglePrimaryKey<*, *>).column.name}"
                // remaining joins
                val joins = joins()

                "EXISTS ( SELECT * FROM ${rootJoinClause.table.declaration} $joins WHERE ${rootJoinClause.field.fieldName} = $joinedTableFieldName"
            } else {
                ""
            }
        }

        /**
         * Try to find a "root" joinClause on one column of the table this query targets
         */
        private fun rootJoinClause() = with(properties) {
            if (joinClauses.isNotEmpty()) {
                val rootJoinClause = joinClauses
                        .firstOrNull { joinClause ->
                            joinClause.field.column.table == table
                                    && JoinType.INNER == joinClause.type
                        }
                        ?: throw IllegalArgumentException("There must be a join clause on one column of the table this query targets")
                require(rootJoinClause.table.primaryKey is SinglePrimaryKey<*, *>) {
                    "Only table with single column primary key is currently supported, ${table.name} is not"
                }
                joinClauses.remove(rootJoinClause)

                rootJoinClause
            } else {
                null
            }
        }
    }
}