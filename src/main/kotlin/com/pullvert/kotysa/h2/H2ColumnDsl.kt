/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*
import com.pullvert.kotysa.ColumnDsl
import com.pullvert.kotysa.DateColumnBuilderNotNullImpl
import com.pullvert.kotysa.DateColumnBuilderNullableImpl
import com.pullvert.kotysa.DateTimeColumnBuilderNotNullImpl
import com.pullvert.kotysa.DateTimeColumnBuilderNullableImpl
import com.pullvert.kotysa.TimestampColumnBuilderNotNullImpl
import com.pullvert.kotysa.TimestampColumnBuilderNullableImpl
import com.pullvert.kotysa.VarcharColumnBuilderNotNullImpl
import com.pullvert.kotysa.VarcharColumnBuilderNullableImpl
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

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNotNull<LocalDateTime> =
            DateTimeColumnBuilderNotNullImpl(property)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNullable =
            DateTimeColumnBuilderNullableImpl(property)

    fun NotNullLocalDateColumnProperty<T>.date(): DateColumnBuilderNotNull<LocalDate> =
            DateColumnBuilderNotNullImpl(property)

    fun NullableLocalDateColumnProperty<T>.date(): DateColumnBuilderNullable =
            DateColumnBuilderNullableImpl(property)

    fun NotNullInstantColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<Instant> =
            TimestampColumnBuilderNotNullImpl(property)

    fun NullableInstantColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
            TimestampColumnBuilderNullableImpl(property)

    fun NotNullLocalTimeColumnProperty<T>.time(): TimeColumnBuilderNotNull<LocalTime> =
            TimeColumnBuilderNotNullImpl(property)

    fun NullableLocalTimeColumnProperty<T>.time(): TimeColumnBuilderNullable =
            TimeColumnBuilderNullableImpl(property)
}
