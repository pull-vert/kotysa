/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa

import com.github.michaelbull.logging.InlineLogger
import com.pullvert.kotysa.h2.h2DeleteFromTableSql
import com.pullvert.kotysa.h2.h2UpdateTableSql
import com.pullvert.kotysa.postgresql.postgresqlUpdateTableSql
import com.pullvert.kotysa.sqlite.sqLiteDeleteFromTableSql
import com.pullvert.kotysa.sqlite.sqLiteUpdateTableSql
import kotlin.reflect.KClass

private val logger = InlineLogger("com.pullvert.kotysa.DefaultSqlClientDeleteOrUpdate")


public open class DefaultSqlClientDeleteOrUpdate protected constructor() : DefaultSqlClientCommon() {

    public class Properties<T : Any> internal constructor(
            override val tables: Tables,
            /**
             * targeted table to update
             */
            public val table: Table<T>,
            override val availableColumns: MutableMap<(Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties {
        override val whereClauses: MutableList<TypedWhereClause> = mutableListOf()
        override val joinClauses: MutableList<JoinClause> = mutableListOf()
        public val setValues: MutableMap<Column<T, *>, Any?> = mutableMapOf()
    }

    public interface WithProperties<T : Any> {
        public val properties: Properties<T>
    }

    protected interface DeleteOrUpdate<T : Any> : Instruction {

        public val tables: Tables
        public val tableClass: KClass<T>

        public fun initProperties(): Properties<T> {
            tables.checkTable(tableClass)
            val table = tables.getTable(tableClass)
            val properties = Properties(tables, table, mutableMapOf())
            // init availableColumns with table columns
            addAvailableColumnsFromTable(properties, table)
            return properties
        }
    }

    protected interface Update<T : Any> : DeleteOrUpdate<T>, WithProperties<T> {
        public fun addSetValue(dsl: (FieldSetter<T>) -> Unit) {
            properties.apply {
                val setValue = UpdateSetDsl(dsl, availableColumns, tables.dbType).initialize()
                setValues[setValue.first.column] = setValue.second
            }
        }
    }

    protected interface Join<T : Any> : DefaultSqlClientCommon.Join, WithProperties<T>, Instruction

    protected interface Where<T : Any> : DefaultSqlClientCommon.Where, WithProperties<T>

    protected interface TypedWhere<T : Any> : DefaultSqlClientCommon.TypedWhere<T>, WithProperties<T>

    public interface Return<T : Any> : DefaultSqlClientCommon.Return, WithProperties<T> {

        public fun deleteFromTableSql(): String = when (properties.tables.dbType) {
            DbType.H2, DbType.POSTGRESQL -> h2DeleteFromTableSql(logger)
            DbType.SQLITE -> sqLiteDeleteFromTableSql(logger)
        }

        public fun updateTableSql(): String = when (properties.tables.dbType) {
            DbType.H2 -> h2UpdateTableSql(logger)
            DbType.POSTGRESQL -> postgresqlUpdateTableSql(logger)
            DbType.SQLITE -> sqLiteUpdateTableSql(logger)
        }

        /**
         * Handle joins as EXISTS + nested SELECT
         * Then other WHERE clauses
         */
        public fun joinsWithExistsAndWheres(withWhere: Boolean = true, offset: Int = 1): String = with(properties) {
            val joins = joinsWithExists()

            var wheres = wheres(false, offset)

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
