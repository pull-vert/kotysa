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

    private val tables = mutableMapOf<KClass<*>, Table<*>>()
    private val allColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()

    @PublishedApi
    internal fun <T : Any> table(tableClass: KClass<T>, dsl: U.() -> Unit) {
        check(!tables.containsKey(tableClass)) {
            "Trying to map entity class \"${tableClass.qualifiedName}\" to multiple tables"
        }
        val table = initializeTable(tableClass, dsl)
        tables[tableClass] = table
        @Suppress("UNCHECKED_CAST")
        allColumns.putAll(table.columns as Map<out (Any) -> Any?, Column<*, *>>)
    }

    protected abstract fun <T : Any> initializeTable(tableClass: KClass<T>, dsl: U.() -> Unit): Table<*>

    internal fun initialize(initialize: T, dbType: DbType): Tables {
        init(initialize)
        require(tables.isNotEmpty()) { "Tables must declare at least one table" }
        val tables = Tables(tables, allColumns, dbType)
        // resolve foreign keys to actual primary key column
        allColumns.filterValues { column -> column.fkClass != null }
                .forEach { (_, column) ->
                    val referencedTable = tables.getTable(column.fkClass!!)
                    val referencedTablePK = referencedTable.primaryKey
                    require(referencedTablePK is SinglePrimaryKey<*, *>) {
                        "Only table with single column primary key is currently supported, ${referencedTable.name} is not"
                    }
                    column.fkColumn = referencedTablePK.column
                }
        return tables
    }
}
