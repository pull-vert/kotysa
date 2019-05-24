/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class TableDsl<T : Any, U : TableDsl<T, U>>(
        private val init: U.() -> Unit,
        private val tableClass: KClass<T>
) {

    lateinit var name: String
    private val columns = mutableMapOf<KProperty1<T, *>, Column<T, *>>()

    internal fun addColumn(column: Column<T, *>) {
        if (columns.containsKey(column.entityProperty)) {
            throw IllegalStateException("Trying to map property \"${column.entityProperty.name}\" to multiple columns")
        }
        require(tableClass.memberProperties.contains(column.entityProperty)) {
            "Trying to map property \"${column.entityProperty.name}\", which is not a property of entity class \"${tableClass.qualifiedName}\""
        }
        columns[column.entityProperty] = column
    }

    @PublishedApi
    internal fun initialize(initialize: U): Table<*> {
        init(initialize)
        require(::name.isInitialized) { "Table name is mandatory" }
        require(columns.isNotEmpty()) { "Table must declare at least one column" }
        require(columns.values.count { column -> column.isPrimaryKey } <= 1) { "Table must not declare more than one Primary Key Column" }
        val table = Table(tableClass, name, columns)
        // associate table to Column
        columns.forEach { (_, c) -> c.table = table }
        return table
    }
}