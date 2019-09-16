/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.*

/**
 * see [SqLite Data types](https://www.sqlite.org/datatype3.html)
 * @author Fred Montariol
 */
class SqLiteColumnDsl<T : Any> internal constructor(
        init: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T>
) : ColumnDsl<T, SqLiteColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.varchar() = VarcharColumnBuilderNotNull(getter)

    fun NullableStringColumnProperty<T>.varchar() = VarcharColumnBuilderNullable(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.dateTime() = DateTimeColumnBuilderNotNull(getter)

    fun NullableLocalDateTimeColumnProperty<T>.dateTime() = DateTimeColumnBuilderNullable(getter)

    fun NotNullLocalDateColumnProperty<T>.date() = DateColumnBuilderNotNull(getter)

    fun NullableLocalDateColumnProperty<T>.date() = DateColumnBuilderNullable(getter)

    fun NotNullBooleanColumnProperty<T>.boolean() = BooleanColumnBuilderNotNull(getter)
}
