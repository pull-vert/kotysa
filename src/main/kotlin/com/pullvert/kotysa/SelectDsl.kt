/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KProperty1

/**
 * All methods return an unused value
 * @author Fred Montariol
 */
class SelectDsl<T> internal constructor(
        private val init: (ValueProvider) -> T,
        override val allColumns: Map<KProperty1<*, *>, Column<*, *>>
) : FieldProvider(), ValueProvider {

    private var fieldIndex = 0
    private val columnPropertyIndexMap = mutableMapOf<KProperty1<*, *>, Int>()
    private val selectedProperties = mutableListOf<KProperty1<*, *>>()
    private val selectedFields = mutableListOf<Field>()
    private val selectedTables = mutableSetOf<Table<*>>()

    override operator fun <T : Any> get(property: KProperty1<T, String>, alias: String?): String {
        val field = getField(property, alias)
        addColumnField(property, field)
        return ""
    }

    override operator fun <T : Any> get(property: KProperty1<T, String?>, alias: String?, `_`: Nullable): String? {
        val field = getField(property, alias)
        addColumnField(property, field)
        return null
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalDateTime>, alias: String?): LocalDateTime {
        val field = getField(property, alias)
        addColumnField(property, field)
        return LocalDateTime.MAX
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalDateTime?>, alias: String?, `_`: Nullable): LocalDateTime? {
        val field = getField(property, alias)
        addColumnField(property, field)
        return null
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalDate>, alias: String?): LocalDate {
        val field = getField(property, alias)
        addColumnField(property, field)
        return LocalDate.MAX
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalDate?>, alias: String?, `_`: Nullable): LocalDate? {
        val field = getField(property, alias)
        addColumnField(property, field)
        return null
    }

    override operator fun <T : Any> get(property: KProperty1<T, Instant>, alias: String?): Instant {
        val field = getField(property, alias)
        addColumnField(property, field)
        return Instant.MAX
    }

    override operator fun <T : Any> get(property: KProperty1<T, Instant?>, alias: String?, `_`: Nullable): Instant? {
        val field = getField(property, alias)
        addColumnField(property, field)
        return null
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalTime>, alias: String?): LocalTime {
        val field = getField(property, alias)
        addColumnField(property, field)
        return LocalTime.MAX
    }

    override operator fun <T : Any> get(property: KProperty1<T, LocalTime?>, alias: String?, `_`: Nullable): LocalTime? {
        val field = getField(property, alias)
        addColumnField(property, field)
        return null
    }

    private fun addColumnField(property: KProperty1<*, *>, columnField: ColumnField<*, *>) {
        addField(property, columnField)
        if (!selectedTables.contains(columnField.column.table)) {
            selectedTables.add(columnField.column.table)
        }
    }

    private fun addField(columnProperty: KProperty1<*, *>, field: Field) {
        require(!selectedProperties.contains(columnProperty)) {
            "This field \"${columnProperty.name}\" is already selected, you cannot select the same field multiple times"
        }
        selectedProperties.add(columnProperty)
        selectedFields.add(field)
        columnPropertyIndexMap[columnProperty] = fieldIndex++
    }

    internal fun initialize(): SelectInformation<T> {
        init(this)
        require(columnPropertyIndexMap.isNotEmpty()) { "Table must declare at least one column" }
        return SelectInformation(columnPropertyIndexMap, selectedFields, selectedTables, init)
    }
}
