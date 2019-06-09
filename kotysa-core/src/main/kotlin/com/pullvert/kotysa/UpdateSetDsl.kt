/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Fred Montariol
 */
@KotysaMarker
class UpdateSetDsl<T : Any> internal constructor(
        private val init: (FieldSetter<T>) -> Unit,
        override val availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldAccess(), FieldSetter<T> {

    private lateinit var columnField: ColumnField<T, *>
    private var value: Any? = null

    override fun set(getter: (T) -> String, value: String) {
        addValue(getField(getter, null), value)
    }

    override fun set(getter: (T) -> String?, value: String?): Nullable {
        addValue(getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalDateTime, value: LocalDateTime) {
        addValue(getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalDateTime?, value: LocalDateTime?): Nullable {
        addValue(getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalDate, value: LocalDate) {
        addValue(getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalDate?, value: LocalDate?): Nullable {
        addValue(getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> Instant, value: Instant) {
        addValue(getField(getter, null), value)
    }

    override fun set(getter: (T) -> Instant?, value: Instant?): Nullable {
        addValue(getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalTime, value: LocalTime) {
        addValue(getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalTime?, value: LocalTime?): Nullable {
        addValue(getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> Boolean, value: Boolean) {
        addValue(getField(getter, null), value)
    }

    private fun addValue(columnField: ColumnField<T, *>, value: Any?) {
        require(!this::columnField.isInitialized) { "Only one value assignment is required" }
        this.columnField = columnField
        this.value = value
    }

    internal fun initialize(): Pair<ColumnField<T, *>, Any?> {
        init(this)
        require(::columnField.isInitialized) { "One value assignment is required" }
        return Pair(columnField, value)
    }
}