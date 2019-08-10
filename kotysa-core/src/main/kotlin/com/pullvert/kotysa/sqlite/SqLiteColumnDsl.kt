/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * see [SqLite Data types](https://www.sqlite.org/datatype3.html)
 * @author Fred Montariol
 */
class SqLiteColumnDsl<T : Any> internal constructor(
        init: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : ColumnDsl<T, SqLiteColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar(): VarcharColumnBuilderNotNull<String> =
            VarcharColumnBuilderNotNullImpl(getter)

    fun NullableStringColumnProperty<T>.varchar(): VarcharColumnBuilderNullable =
            VarcharColumnBuilderNullableImpl(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNotNull<LocalDateTime> =
            DateTimeColumnBuilderNotNullImpl(getter)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime(): DateTimeColumnBuilderNullable =
            DateTimeColumnBuilderNullableImpl(getter)

    fun NotNullLocalDateColumnProperty<T>.date(): DateColumnBuilderNotNull<LocalDate> =
            DateColumnBuilderNotNullImpl(getter)

    fun NullableLocalDateColumnProperty<T>.date(): DateColumnBuilderNullable =
            DateColumnBuilderNullableImpl(getter)

    fun NotNullBooleanColumnProperty<T>.boolean(): BooleanColumnBuilderNotNull<Boolean> =
            BooleanColumnBuilderNotNullImpl(getter)
}
