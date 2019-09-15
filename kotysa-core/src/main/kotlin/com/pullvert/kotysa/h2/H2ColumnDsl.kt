/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * see [H2 Data types](http://h2database.com/html/datatypes.html)
 * @author Fred Montariol
 */
class H2ColumnDsl<T : Any> internal constructor(
        init: H2ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T>
) : ColumnDsl<T, H2ColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar() =
            VarcharColumnBuilderNotNull(getter)

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

    fun NotNullUuidColumnProperty<T>.uuid(): UuidColumnBuilderNotNull<UUID> =
            UuidColumnBuilderNotNullImpl(getter)

    fun NullableUuidColumnProperty<T>.uuid(): UuidColumnBuilderNullable =
            UuidColumnBuilderNullableImpl(getter)
}
