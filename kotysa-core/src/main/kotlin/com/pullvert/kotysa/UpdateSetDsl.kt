/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
@KotysaMarker
class UpdateSetDsl<T : Any> internal constructor(
        private val init: (FieldSetter<T>) -> Unit,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldSetter<T> {

    private val fieldAccess = FieldAccess(availableColumns)
    private lateinit var columnField: ColumnField<T, *>
    private var value: Any? = null

    override fun set(getter: (T) -> String, value: String) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> String?, value: String?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalDateTime, value: LocalDateTime) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalDateTime?, value: LocalDateTime?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalDate, value: LocalDate) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalDate?, value: LocalDate?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> ZonedDateTime, value: ZonedDateTime) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> ZonedDateTime?, value: ZonedDateTime?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> LocalTime, value: LocalTime) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> LocalTime?, value: LocalTime?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
    }

    override fun set(getter: (T) -> Boolean, value: Boolean) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> UUID, value: UUID) {
        addValue(fieldAccess.getField(getter, null), value)
    }

    override fun set(getter: (T) -> UUID?, value: UUID?): Nullable {
        addValue(fieldAccess.getField(getter, null), value)
        return Nullable.TRUE
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