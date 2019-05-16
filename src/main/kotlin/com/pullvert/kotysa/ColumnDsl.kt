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
class ColumnDsl<T : Any>(private val init: ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>) {

	fun NotNullStringColumnProperty<T>.varchar(): VarcharColumnBuilderNotNull<String> =
			VarcharColumnBuilderNotNullImpl(property)

	fun NullableStringColumnProperty<T>.varchar(): VarcharColumnBuilderNullable =
			VarcharColumnBuilderNullableImpl(property)

	fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<LocalDateTime> =
			TimestampColumnBuilderNotNullImpl(property)

	fun NullableLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
			TimestampColumnBuilderNullableImpl(property)

	fun NotNullDateColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<Date> =
			TimestampColumnBuilderNotNullImpl(property)

	fun NullableDateColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
			TimestampColumnBuilderNullableImpl(property)

	fun NotNullDateColumnProperty<T>.date(): DateColumnBuilderNotNull<Date> =
			DateColumnBuilderNotNullImpl(property)

	fun NullableDateColumnProperty<T>.date(): DateColumnBuilderNullable =
			DateColumnBuilderNullableImpl(property)

	@Suppress("UNCHECKED_CAST")
	internal fun initialize(): Column<T, *> {
		val columnBuilder = init(TableColumnPropertyProviderImpl()) as AbstractColumnBuilder<*, T>
		if (!columnBuilder.columnNameInitialized) {
			columnBuilder.columnName = columnBuilder.entityProperty.name
		}
		return columnBuilder.build()
	}
}
