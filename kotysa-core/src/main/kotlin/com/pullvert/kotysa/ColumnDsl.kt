/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KFunction

/**
 * @author Fred Montariol
 */
@KotysaMarker
class ColumnDsl<T : Any> internal constructor(
        private val init: ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : TableColumnPropertyProvider<T> {

    fun NotNullStringColumnProperty<T>.varchar(): VarcharColumnBuilderNotNull<String> =
            VarcharColumnBuilderNotNullImpl(getter)

    fun NullableStringColumnProperty<T>.varchar(): VarcharColumnBuilderNullable =
            VarcharColumnBuilderNullableImpl(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<LocalDateTime> =
            TimestampColumnBuilderNotNullImpl(getter)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
            TimestampColumnBuilderNullableImpl(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNotNull<LocalDateTime> =
            DateTimeColumnBuilderNotNullImpl(getter)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNullable =
            DateTimeColumnBuilderNullableImpl(getter)

    fun NotNullLocalDateColumnProperty<T>.date(): DateColumnBuilderNotNull<LocalDate> =
            DateColumnBuilderNotNullImpl(getter)

    fun NullableLocalDateColumnProperty<T>.date(): DateColumnBuilderNullable =
            DateColumnBuilderNullableImpl(getter)

    fun NotNullInstantColumnProperty<T>.timestampWithTimeZone(): TimestampWithTimeZoneColumnBuilderNotNull<Instant> =
            TimestampWithTimeZoneColumnBuilderNotNullImpl(getter)

    fun NullableInstantColumnProperty<T>.timestampWithTimeZone(): TimestampWithTimeZoneColumnBuilderNullable =
            TimestampWithTimeZoneColumnBuilderNullableImpl(getter)

    fun NotNullLocalTimeColumnProperty<T>.time9(): Time9ColumnBuilderNotNull<LocalTime> =
            Time9ColumnBuilderNotNullImpl(getter)

    fun NullableLocalTimeColumnProperty<T>.time9(): Time9ColumnBuilderNullable =
            Time9ColumnBuilderNullableImpl(getter)

    fun NotNullBooleanColumnProperty<T>.boolean(): BooleanColumnBuilderNotNull<Boolean> =
            BooleanColumnBuilderNotNullImpl(getter)

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
        if (getter !is KFunction<*>) {
            require(getter.toCallable().returnType.isMarkedNullable) { "\"$getter\" doesn't have a nullable return type" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): Column<T, *> {
        val columnBuilder = init(this, this) as AbstractColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityGetter.toCallable().name
        }
        return columnBuilder.build()
    }
}
