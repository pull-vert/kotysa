/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.postgresql

import com.pullvert.kotysa.*
import com.pullvert.kotysa.h2.TimestampWithTimeZoneColumnBuilderNotNull
import com.pullvert.kotysa.h2.TimestampWithTimeZoneColumnBuilderNullable

/**
 * see [Postgres Data types](https://www.postgresql.org/docs/11/datatype.html)
 * @author Fred Montariol
 */
class PostgresqlColumnDsl<T : Any> internal constructor(
        init: PostgresqlColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T, *>
) : ColumnDsl<T, PostgresqlColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar() = VarcharColumnBuilderNotNull(getter)

    fun NullableStringColumnProperty<T>.varchar() = VarcharColumnBuilderNullable(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp() = TimestampColumnBuilderNotNull(getter)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp() = TimestampColumnBuilderNullable(getter)

    fun NotNullLocalDateColumnProperty<T>.date() = DateColumnBuilderNotNull(getter)

    fun NullableLocalDateColumnProperty<T>.date() = DateColumnBuilderNullable(getter)

    fun NotNullOffsetDateTimeColumnProperty<T>.timestampWithTimeZone() = TimestampWithTimeZoneColumnBuilderNotNull(getter)

    fun NullableOffsetDateTimeColumnProperty<T>.timestampWithTimeZone() = TimestampWithTimeZoneColumnBuilderNullable(getter)

    fun NotNullLocalTimeColumnProperty<T>.time() = TimeColumnBuilderNotNull(getter)

    fun NullableLocalTimeColumnProperty<T>.time() = TimeColumnBuilderNullable(getter)

    fun NotNullBooleanColumnProperty<T>.boolean() = BooleanColumnBuilderNotNull(getter)

    fun NotNullUuidColumnProperty<T>.uuid() = UuidColumnBuilderNotNull(getter)

    fun NullableUuidColumnProperty<T>.uuid() = UuidColumnBuilderNullable(getter)

    fun NotNullIntColumnProperty<T>.integer() = IntegerNoAutoIncrementColumnBuilderNotNull(getter)

    fun NullableIntColumnProperty<T>.integer() = IntegerNoAutoIncrementColumnBuilderNullable(getter)

    fun NotNullIntColumnProperty<T>.serial() = SerialColumnBuilder(getter)

    fun NullableIntColumnProperty<T>.serial() = SerialColumnBuilder(getter)
}
