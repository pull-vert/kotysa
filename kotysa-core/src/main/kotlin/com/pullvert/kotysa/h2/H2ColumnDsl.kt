/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Fred Montariol
 */
class H2ColumnDsl<T : Any> internal constructor(
        init: H2ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : ColumnDsl<T, H2ColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar(): VarcharColumnBuilderNotNull<String> =
            VarcharColumnBuilderNotNullImpl(property)

    fun NullableStringColumnProperty<T>.varchar(): VarcharColumnBuilderNullable =
            VarcharColumnBuilderNullableImpl(property)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<LocalDateTime> =
            TimestampColumnBuilderNotNullImpl(property)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
            TimestampColumnBuilderNullableImpl(property)

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNotNull<LocalDateTime> =
            DateTimeColumnBuilderNotNullImpl(property)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNullable =
            DateTimeColumnBuilderNullableImpl(property)

    fun NotNullLocalDateColumnProperty<T>.date(): DateColumnBuilderNotNull<LocalDate> =
            DateColumnBuilderNotNullImpl(property)

    fun NullableLocalDateColumnProperty<T>.date(): DateColumnBuilderNullable =
            DateColumnBuilderNullableImpl(property)

    fun NotNullInstantColumnProperty<T>.timestampWithTimeZone(): TimestampWithTimeZoneColumnBuilderNotNull<Instant> =
            TimestampWithTimeZoneColumnBuilderNotNullImpl(property)

    fun NullableInstantColumnProperty<T>.timestampWithTimeZone(): TimestampWithTimeZoneColumnBuilderNullable =
            TimestampWithTimeZoneColumnBuilderNullableImpl(property)

    fun NotNullLocalTimeColumnProperty<T>.time9(): Time9ColumnBuilderNotNull<LocalTime> =
            Time9ColumnBuilderNotNullImpl(property)

    fun NullableLocalTimeColumnProperty<T>.time9(): Time9ColumnBuilderNullable =
            Time9ColumnBuilderNullableImpl(property)

    fun NotNullBooleanColumnProperty<T>.boolean(): BooleanColumnBuilderNotNull<Boolean> =
            BooleanColumnBuilderNotNullImpl(property)
}
