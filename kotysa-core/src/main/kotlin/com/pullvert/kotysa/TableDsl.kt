/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.reflect

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class TableDsl<T : Any, U : TableDsl<T, U>>(
        private val init: U.() -> Unit,
        private val tableClass: KClass<T>
) {

    lateinit var name: String
    private val columns = mutableMapOf<(T) -> Any?, Column<T, *>>()

    internal fun addColumn(column: Column<T, *>) {
        if (columns.containsKey(column.entityGetter)) {
            throw IllegalStateException("Trying to map property \"${column.entityGetter.reflect()!!.name}\" to multiple columns")
        }
        require(tableClass.memberProperties.contains(column.entityGetter)) {
            "Trying to map property \"${column.entityGetter.reflect()!!.name}\", which is not a property of entity class \"${tableClass.qualifiedName}\""
        }
        columns[column.entityGetter] = column
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
