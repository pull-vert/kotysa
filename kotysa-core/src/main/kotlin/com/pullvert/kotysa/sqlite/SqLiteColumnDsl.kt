/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

/**
 * see [SqLite Data types](https://www.sqlite.org/datatype3.html)
 * @author Fred Montariol
 */
public class SqLiteColumnDsl<T : Any> internal constructor(
        init: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>
) : ColumnDsl<T, SqLiteColumnDsl<T>>(init) {

    public fun NotNullStringColumnProperty<T>.text(): TextColumnBuilderNotNull<T, String> =
            TextColumnBuilderNotNull(getter)

    public fun NullableStringColumnProperty<T>.text(): TextColumnBuilderNullable<T, String> =
            TextColumnBuilderNullable(getter)

    public fun NotNullLocalDateTimeColumnProperty<T>.text(): TextColumnBuilderNotNull<T, LocalDateTime> =
            TextColumnBuilderNotNull(getter)

    public fun NullableLocalDateTimeColumnProperty<T>.text(): TextColumnBuilderNullable<T, LocalDateTime> =
            TextColumnBuilderNullable(getter)

    public fun NotNullLocalDateColumnProperty<T>.text(): TextColumnBuilderNotNull<T, LocalDate> =
            TextColumnBuilderNotNull(getter)

    public fun NullableLocalDateColumnProperty<T>.text(): TextColumnBuilderNullable<T, LocalDate> =
            TextColumnBuilderNullable(getter)

    public fun NotNullOffsetDateTimeColumnProperty<T>.text(): TextColumnBuilderNotNull<T, OffsetDateTime> =
            TextColumnBuilderNotNull(getter)

    public fun NullableOffsetDateTimeColumnProperty<T>.text(): TextColumnBuilderNullable<T, OffsetDateTime> =
            TextColumnBuilderNullable(getter)

    public fun NotNullLocalTimeColumnProperty<T>.text(): TextColumnBuilderNotNull<T, LocalTime> =
            TextColumnBuilderNotNull(getter)

    public fun NullableLocalTimeColumnProperty<T>.text(): TextColumnBuilderNullable<T, LocalTime> =
            TextColumnBuilderNullable(getter)

    public fun NotNullBooleanColumnProperty<T>.integer(): IntegerColumnBuilderNotNull<T, Boolean> =
            IntegerColumnBuilderNotNull(getter)

    public fun NotNullIntColumnProperty<T>.integer(): IntegerColumnBuilderNotNull<T, Int> =
            IntegerColumnBuilderNotNull(getter)

    public fun NullableIntColumnProperty<T>.integer(): IntegerColumnBuilderNullable<T, Int> =
            IntegerColumnBuilderNullable(getter)
}
