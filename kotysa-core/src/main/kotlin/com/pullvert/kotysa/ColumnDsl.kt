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
abstract class ColumnDsl<T : Any, U : ColumnDsl<T, U>> internal constructor(
        private val init: U.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : TableColumnPropertyProvider<T> {

    override fun get(getter: (T) -> String) = NotNullStringColumnProperty(getter)

    override fun get(getter: (T) -> String?): NullableStringColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableStringColumnProperty(getter)
    }

    override fun get(getter: (T) -> LocalDateTime) = NotNullLocalDateTimeColumnProperty(getter)

    override fun get(getter: (T) -> LocalDateTime?): NullableLocalDateTimeColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableLocalDateTimeColumnProperty(getter)
    }

    override fun get(getter: (T) -> LocalDate) = NotNullLocalDateColumnProperty(getter)

    override fun get(getter: (T) -> LocalDate?): NullableLocalDateColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableLocalDateColumnProperty(getter)
    }

    override fun get(getter: (T) -> Instant) = NotNullInstantColumnProperty(getter)

    override fun get(getter: (T) -> Instant?): NullableInstantColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableInstantColumnProperty(getter)
    }

    override fun get(getter: (T) -> LocalTime) = NotNullLocalTimeColumnProperty(getter)

    override fun get(getter: (T) -> LocalTime?): NullableLocalTimeColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableLocalTimeColumnProperty(getter)
    }

    override fun get(getter: (T) -> Boolean) = NotNullBooleanColumnProperty(getter)

    private fun checkNullableGetter(getter: (T) -> Any?) {
        require(getter.toCallable().returnType.isMarkedNullable) { "\"$getter\" doesn't have a nullable return type" }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(initialize: U): Column<T, *> {
        val columnBuilder = init(initialize, initialize) as AbstractColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityGetter.toCallable().name
        }
        return columnBuilder.build()
    }
}
