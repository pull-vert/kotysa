/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@KotysaMarker
class ColumnDsl<T : Any> internal constructor(
        private val init: ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : ColumnPropertyProvider(), TableColumnPropertyProvider<T> {

    fun NotNullStringColumnProperty<T>.varchar(): VarcharColumnBuilderNotNull<String> =
            VarcharColumnBuilderNotNullImpl(property)

    fun NullableStringColumnProperty<T>.varchar(): VarcharColumnBuilderNullable =
            VarcharColumnBuilderNullableImpl(property)

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNotNull<LocalDateTime> =
            TimestampColumnBuilderNotNullImpl(property)

    fun NullableLocalDateTimeColumnProperty<T>.timestamp(): TimestampColumnBuilderNullable =
            TimestampColumnBuilderNullableImpl(property)

    fun NotNullLocalDateColumnProperty<T>.date(): DateColumnBuilderNotNull<LocalDate> =
            DateColumnBuilderNotNullImpl(property)

    fun NullableLocalDateColumnProperty<T>.date(): DateColumnBuilderNullable =
            DateColumnBuilderNullableImpl(property)

    override fun get(stringProperty: KProperty1<T, String>) = colProp(stringProperty)

    override fun get(localDateTimeProperty: KProperty1<T, LocalDateTime>) = colProp(localDateTimeProperty)

    override fun get(dateProperty: KProperty1<T, LocalDate>) = colProp(dateProperty)

    override fun get(nullableStringProperty: KProperty1<T, String?>) = colProp(nullableStringProperty)

    override fun get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>) = colProp(nullableLocalDateTimeProperty)

    override fun get(nullableDateProperty: KProperty1<T, LocalDate?>) = colProp(nullableDateProperty)

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): Column<T, *> {
        val columnBuilder = init(this) as AbstractColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityProperty.name
        }
        return columnBuilder.build()
    }
}
