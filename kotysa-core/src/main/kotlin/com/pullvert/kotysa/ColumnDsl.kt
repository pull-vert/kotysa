/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.reflect.KFunction

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class ColumnDsl<T : Any, U : ColumnDsl<T, U>> internal constructor(
        private val init: U.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>
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

    override fun get(getter: (T) -> OffsetDateTime) = NotNullOffsetDateTimeColumnProperty(getter)

    override fun get(getter: (T) -> OffsetDateTime?): NullableOffsetDateTimeColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableOffsetDateTimeColumnProperty(getter)
    }

    override fun get(getter: (T) -> LocalTime) = NotNullLocalTimeColumnProperty(getter)

    override fun get(getter: (T) -> LocalTime?): NullableLocalTimeColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableLocalTimeColumnProperty(getter)
    }

    override fun get(getter: (T) -> Boolean) = NotNullBooleanColumnProperty(getter)

    override fun get(getter: (T) -> UUID) = NotNullUuidColumnProperty(getter)

    override fun get(getter: (T) -> UUID?): NullableUuidColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableUuidColumnProperty(getter)
    }

    override fun get(getter: (T) -> Int) = NotNullIntColumnProperty(getter)

    override fun get(getter: (T) -> Int?): NullableIntColumnProperty<T> {
        checkNullableGetter(getter)
        return NullableIntColumnProperty(getter)
    }

    private fun checkNullableGetter(getter: (T) -> Any?) {
        if (getter !is KFunction<*>) {
            require(getter.toCallable().returnType.isMarkedNullable) { "\"$getter\" doesn't have a nullable return type" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(initialize: U): Column<T, *> {
        val columnBuilder = init(initialize, initialize)
        if (!columnBuilder.isColumnNameInitialized) {
            columnBuilder.props.columnName = columnBuilder.props.entityGetter.toCallable().name
        }
        return columnBuilder.build()
    }
}
