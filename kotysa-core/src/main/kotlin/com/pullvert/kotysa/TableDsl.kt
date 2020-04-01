/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
@KotysaMarker
public abstract class TableDsl<T : Any, U : TableDsl<T, U>>(
		private val init: U.() -> Unit,
		private val tableClass: KClass<T>
) {

	public lateinit var name: String
	private val columns = mutableMapOf<(T) -> Any?, Column<T, *>>()
	public lateinit var primaryKey: PrimaryKey

	protected fun addColumn(column: Column<T, *>) {
		require(!columns.containsKey(column.entityGetter)) {
			"Trying to map property \"${column.entityGetter}\" to multiple columns"
		}
		require(tableClass.members.contains(column.entityGetter.toCallable())) {
			"Trying to map property \"${column.entityGetter}\", which is not a property of entity class \"${tableClass.qualifiedName}\""
		}
		if (column.isPrimaryKey) {
			check(!::primaryKey.isInitialized) {
				"Table must not declare more than one Primary Key"
			}
			primaryKey = SinglePrimaryKey(column.pkName, column)
		}
		columns[column.entityGetter] = column
	}

	@PublishedApi
	internal fun initialize(initialize: U): Table<*> {
		init(initialize)
		if (!::name.isInitialized) {
			name = tableClass.simpleName!!
		}
		require(::primaryKey.isInitialized) { "Table primary key is mandatory" }
		require(columns.isNotEmpty()) { "Table must declare at least one column" }
		val table = TableImpl(tableClass, name, columns, primaryKey)
		// associate table to all its columns
		columns.forEach { (_, c) -> c.table = table }
		return table
	}
}
