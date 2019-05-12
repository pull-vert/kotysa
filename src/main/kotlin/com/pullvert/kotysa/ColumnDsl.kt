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
class ColumnDsl<T : Any>(private val init: ColumnDsl<T>.(ColumnChoiceProvider<T>) -> Column.ColumnBuilder<*>) {

	fun NotNullStringColumnChoice<T>.varchar(): VarcharColumnNotNull.ColumnBuilder<String> =
			VarcharColumnNotNull.ColumnBuilderImpl(property)

	fun NullableStringColumnChoice<T>.varchar(): VarcharColumnNullable.ColumnBuilder =
			VarcharColumnNullable.ColumnBuilderImpl(property)

	fun NotNullLocalDateTimeColumnChoice<T>.timestamp(): TimestampColumnNotNull.ColumnBuilder<LocalDateTime> =
			TimestampColumnNotNull.ColumnBuilderImpl(property)

	fun NullableLocalDateTimeColumnChoice<T>.timestamp(): TimestampColumnNullable.ColumnBuilder =
			TimestampColumnNullable.ColumnBuilderImpl(property)

	fun NotNullDateColumnChoice<T>.timestamp(): TimestampColumnNotNull.ColumnBuilder<Date> =
			TimestampColumnNotNull.ColumnBuilderImpl(property)

	fun NullableDateColumnChoice<T>.timestamp(): TimestampColumnNullable.ColumnBuilder =
			TimestampColumnNullable.ColumnBuilderImpl(property)

	fun NotNullDateColumnChoice<T>.date(): DateColumnNotNull.ColumnBuilder<Date> =
			DateColumnNotNull.ColumnBuilderImpl(property)

	fun NullableDateColumnChoice<T>.date(): DateColumnNullable.ColumnBuilder =
			DateColumnNullable.ColumnBuilderImpl(property)

	@Suppress("UNCHECKED_CAST")
	fun initialize(): Column<T, *> {
		val columnBuilder = init(ColumnChoiceProvider()) as AbstractColumn.ColumnBuilder<*, T>
		if (!columnBuilder.columnNameInitialized) {
			columnBuilder.columnName = columnBuilder.entityProperty.name
		}
		return columnBuilder.build()
	}
}
