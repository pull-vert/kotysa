/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sqlite

import com.pullvert.kotysa.*
import com.pullvert.kotysa.h2.Time9ColumnBuilderNotNull
import com.pullvert.kotysa.h2.Time9ColumnBuilderNullable
import com.pullvert.kotysa.h2.TimestampWithTimeZoneColumnBuilderNotNull
import com.pullvert.kotysa.h2.TimestampWithTimeZoneColumnBuilderNullable

/**
 * see [SqLite Data types](https://www.sqlite.org/datatype3.html)
 * @author Fred Montariol
 */
class SqLiteColumnDsl<T : Any> internal constructor(
        init: SqLiteColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*, T>
) : ColumnDsl<T, SqLiteColumnDsl<T>>(init) {

    fun NotNullStringColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NullableStringColumnProperty<T>.text() = TextColumnBuilderNullable(getter)

    fun NotNullLocalDateTimeColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NullableLocalDateTimeColumnProperty<T>.text() = TextColumnBuilderNullable(getter)

    fun NotNullLocalDateColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NullableLocalDateColumnProperty<T>.text() = TextColumnBuilderNullable(getter)

    fun NotNullOffsetDateTimeColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NullableOffsetDateTimeColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NotNullLocalTimeColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NullableLocalTimeColumnProperty<T>.text() = TextColumnBuilderNotNull(getter)

    fun NotNullBooleanColumnProperty<T>.integer() = IntegerColumnBuilderNotNull(getter)
}
