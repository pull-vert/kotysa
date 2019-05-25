/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * All methods return an unused value
 * @author Fred Montariol
 */
class SelectDsl<T> internal constructor(
        private val init: (ValueProvider) -> T,
        override val availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldProvider(), ValueProvider {

    private var fieldIndex = 0
    private val columnPropertyIndexMap = mutableMapOf<(Any) -> Any?, Int>()
    private val selectedGetters = mutableListOf<(Any) -> Any?>()
    private val selectedFields = mutableListOf<Field>()
    private val selectedTables = mutableSetOf<Table<*>>()

    override operator fun <T : Any> get(getter: (T) -> String, alias: String?): String {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return ""
    }

    override operator fun <T : Any> get(getter: (T) -> String?, alias: String?, `_`: Nullable): String? {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return null
    }

    override operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String?): LocalDateTime {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return LocalDateTime.MAX
    }

    override operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String?, `_`: Nullable): LocalDateTime? {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return null
    }

    override operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String?): LocalDate {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return LocalDate.MAX
    }

    override operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String?, `_`: Nullable): LocalDate? {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return null
    }

    override operator fun <T : Any> get(getter: (T) -> Instant, alias: String?): Instant {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return Instant.MAX
    }

    override operator fun <T : Any> get(getter: (T) -> Instant?, alias: String?, `_`: Nullable): Instant? {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return null
    }

    override operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String?): LocalTime {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return LocalTime.MAX
    }

    override operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String?, `_`: Nullable): LocalTime? {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return null
    }

    override operator fun <T : Any> get(getter: (T) -> Boolean, alias: String?): Boolean {
        val field = getField(getter, alias)
        addColumnField(getter, field)
        return false
    }

    private fun <T : Any> addColumnField(getter: (T) -> Any?, columnField: ColumnField<*, *>) {
        addField(getter, columnField)
        if (!selectedTables.contains(columnField.column.table)) {
            selectedTables.add(columnField.column.table)
        }
    }

    private fun <T : Any> addField(getter: (T) -> Any?, field: Field) {
        require(!selectedGetters.contains(getter)) {
            "\"$getter\" is already selected, you cannot select the same field multiple times"
        }
        @Suppress("UNCHECKED_CAST")
        selectedGetters.add(getter as (Any) -> Any?)
        selectedFields.add(field)
        columnPropertyIndexMap[getter] = fieldIndex++
    }

    internal fun initialize(): SelectInformation<T> {
        init(this)
        require(columnPropertyIndexMap.isNotEmpty()) { "Table must declare at least one column" }
        return SelectInformation(columnPropertyIndexMap, selectedFields, selectedTables, init)
    }
}
