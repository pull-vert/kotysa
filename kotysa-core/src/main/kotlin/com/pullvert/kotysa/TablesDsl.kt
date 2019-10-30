/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class TablesDsl<T : TablesDsl<T, U>, U : TableDsl<*, *>>(private val init: T.() -> Unit) {

    private val allTables = mutableMapOf<KClass<*>, Table<*>>()
    private val allColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()

    @PublishedApi
    internal fun <T : Any> table(tableClass: KClass<T>, dsl: U.() -> Unit) {
        check(!allTables.containsKey(tableClass)) {
            "Trying to map entity class \"${tableClass.qualifiedName}\" to multiple tables"
        }
        val table = initializeTable(tableClass, dsl)
        allTables[tableClass] = table
        @Suppress("UNCHECKED_CAST")
        allColumns.putAll(table.columns as Map<out (Any) -> Any?, Column<*, *>>)
    }

    protected abstract fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: U.() -> Unit): Table<*>

    internal fun initialize(initialize: T, dbType: DbType): Tables {
        init(initialize)
        require(allTables.isNotEmpty()) { "Tables must declare at least one table" }
        val tables = Tables(allTables, allColumns, dbType)

        // resolve foreign keys to referenced primary key column
        resolveFkReferencedColumn(allColumns, tables)

        // build foreign keys
        buildForeignKeys(allTables)

        return tables
    }

    private fun resolveFkReferencedColumn(allColumns: MutableMap<(Any) -> Any?, Column<*, *>>, tables: Tables) {
        allColumns.filterValues { column -> column.fkClass != null }
                .forEach { (_, column) ->
                    val referencedTable = tables.getTable(column.fkClass!!)
                    val referencedTablePK = referencedTable.primaryKey
                    require(referencedTablePK is SinglePrimaryKey<*, *>) {
                        "Only table with single column primary key is currently supported, ${referencedTable.name} is not"
                    }
                    column.fkColumn = referencedTablePK.column
                }
    }

    private fun buildForeignKeys(allTables: MutableMap<KClass<*>, Table<*>>) {
        val foreignKeyNames = mutableSetOf<String>()
        allTables.values.forEach { table ->
            val foreignKeys = mutableSetOf<ForeignKey>()
            // first loop with user provided FK names
            table.columns.values.mapNotNull { column -> column.fkName }
                    .forEach { fkName ->
                        require(!foreignKeyNames.contains(fkName)) {
                            "Foreign key names must be unique, $fkName is duplicated"
                        }
                        foreignKeyNames.add(fkName)
                    }

            // then complete loop on all foreign keys
            table.columns.filterValues { column -> column.fkColumn != null }
                    .forEach { (_, column) ->
                        // build foreign key name if needed
                        val fkName = column.fkName ?: generateFkName(column.table.name, column.fkColumn!!.table.name, foreignKeyNames)
                        foreignKeys.add(SingleForeignKey(fkName, column, column.fkColumn!!))
                    }
            table.foreignKeys = foreignKeys
        }
    }

    private fun generateFkName(tableName: String, referencedTableName: String, foreignKeyNames: MutableSet<String>): String {
        val fkPrefix = "FK_${referencedTableName}_$tableName"
        var generatedFkName = fkPrefix
        if (foreignKeyNames.contains(generatedFkName)) {
            // must concat with index to avoid duplicate foreign key name
            var index = 2
            generatedFkName = "${fkPrefix}_$index"
            while (foreignKeyNames.contains(generatedFkName)) {
                generatedFkName = "${fkPrefix}_${++index}"
            }
        }
        // add this new name to foreignKeyNames Set
        foreignKeyNames.add(generatedFkName)
        return generatedFkName
    }
}
