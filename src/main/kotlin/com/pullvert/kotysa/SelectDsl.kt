/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KProperty1

/**
 * All methods return an unused value
 * @author Fred Montariol
 */
class SelectDsl<T> internal constructor(private val init: (ValueProvider) -> T, override val allColumns: Map<KProperty1<*, *>, Column<*, *>>) : FieldProvider(), ValueProvider {

    private var fieldIndex = 0
    private val columnPropertyIndexMap = mutableMapOf<KProperty1<*, *>, Int>()
    private val selectedProperties = mutableListOf<KProperty1<*, *>>()
    private val selectedFields = mutableListOf<Field>()
    private val selectedTables = mutableSetOf<Table<*>>()

    override operator fun get(stringProperty: KProperty1<*, String>): String {
        val field = getField(stringProperty)
        addColumnField(stringProperty, field)
        return ""
    }

    override operator fun get(nullableStringProperty: KProperty1<*, String?>, `_`: Nullable): String? {
        val field = getField(nullableStringProperty)
        addColumnField(nullableStringProperty, field)
        return null
    }

    override operator fun get(localDateTimeProperty: KProperty1<*, LocalDateTime>): LocalDateTime {
        val field = getField(localDateTimeProperty)
        addColumnField(localDateTimeProperty, field)
        return LocalDateTime.MAX
    }

    override operator fun get(nullableLocalDateTimeProperty: KProperty1<*, LocalDateTime?>, `_`: Nullable): LocalDateTime? {
        val field = getField(nullableLocalDateTimeProperty)
        addColumnField(nullableLocalDateTimeProperty, field)
        return null
    }

    override operator fun get(dateProperty: KProperty1<*, Date>): Date {
        val field = getField(dateProperty)
        addColumnField(dateProperty, field)
        return Date()
    }

    override operator fun get(nullableDateProperty: KProperty1<*, Date?>, `_`: Nullable): Date? {
        val field = getField(nullableDateProperty)
        addColumnField(nullableDateProperty, field)
        return null
    }

    private fun addColumnField(columnProperty: KProperty1<*, *>, columnField: ColumnField<*, *>) {
        addField(columnProperty, columnField)
        if (!selectedTables.contains(columnField.table)) {
            selectedTables.add(columnField.table)
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

/**
 * @author Fred Montariol
 */
data class SelectInformation<T>(
        internal val columnPropertyIndexMap: Map<KProperty1<*, *>, Int>,
        internal val selectedFields: List<Field>,
        internal val selectedTables: Set<Table<*>>,
        internal val select: (ValueProvider) -> T
)
