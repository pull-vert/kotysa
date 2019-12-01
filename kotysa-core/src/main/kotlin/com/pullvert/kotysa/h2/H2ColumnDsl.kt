/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*

/**
 * see [H2 Data types](http://h2database.com/html/datatypes.html)
 * @author Fred Montariol
 */
class H2ColumnDsl<T : Any> internal constructor(
        init: H2ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>
) : ColumnDsl<T, H2ColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar() = VarcharColumnBuilderNotNull(getter)

    fun NullableStringColumnProperty<T>.varchar() = VarcharColumnBuilderNullable(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp() = TimestampColumnBuilderNotNull(getter)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp() = TimestampColumnBuilderNullable(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime() = DateTimeColumnBuilderNotNull(getter)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime() = DateTimeColumnBuilderNullable(getter)

    fun NotNullLocalDateColumnProperty<T>.date() = DateColumnBuilderNotNull(getter)

    fun NullableLocalDateColumnProperty<T>.date() = DateColumnBuilderNullable(getter)

    fun NotNullOffsetDateTimeColumnProperty<T>.timestampWithTimeZone() = TimestampWithTimeZoneColumnBuilderNotNull(getter)

    fun NullableOffsetDateTimeColumnProperty<T>.timestampWithTimeZone() = TimestampWithTimeZoneColumnBuilderNullable(getter)

    fun NotNullLocalTimeColumnProperty<T>.time9() = Time9ColumnBuilderNotNull(getter)

    fun NullableLocalTimeColumnProperty<T>.time9() = Time9ColumnBuilderNullable(getter)

    fun NotNullBooleanColumnProperty<T>.boolean() = BooleanColumnBuilderNotNull(getter)

    fun NotNullUuidColumnProperty<T>.uuid() = UuidColumnBuilderNotNull(getter)

    fun NullableUuidColumnProperty<T>.uuid() = UuidColumnBuilderNullable(getter)

    fun NotNullIntColumnProperty<T>.int() = IntegerColumnBuilderNotNull(getter)

    fun NullableIntColumnProperty<T>.int() = IntegerColumnBuilderNullable(getter)
}
