/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
@KotysaMarker
class WhereDsl<T : Any>(private val init: WhereDsl<T>.(WhereColumnPropertyProvider) -> Column.ColumnBuilder<*>) {

    infix fun NotNullStringColumnProperty<T>.EQ(stringValue: String): VarcharColumnNotNull.ColumnBuilder<String> =
            VarcharColumnNotNull.ColumnBuilderImpl(property)

    fun NullableStringColumnProperty<T>.varchar(): VarcharColumnNullable.ColumnBuilder =
            VarcharColumnNullable.ColumnBuilderImpl(property)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnNotNull.ColumnBuilder<LocalDateTime> =
            TimestampColumnNotNull.ColumnBuilderImpl(property)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnNullable.ColumnBuilder =
            TimestampColumnNullable.ColumnBuilderImpl(property)

    fun NotNullDateColumnProperty<T>.timestamp(): TimestampColumnNotNull.ColumnBuilder<Date> =
            TimestampColumnNotNull.ColumnBuilderImpl(property)

    fun NullableDateColumnProperty<T>.timestamp(): TimestampColumnNullable.ColumnBuilder =
            TimestampColumnNullable.ColumnBuilderImpl(property)

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): Column<T, *> {
        val columnBuilder = init(WhereColumnPropertyProviderImpl()) as AbstractColumn.ColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityProperty.name
        }
        return columnBuilder.build()
    }
}
