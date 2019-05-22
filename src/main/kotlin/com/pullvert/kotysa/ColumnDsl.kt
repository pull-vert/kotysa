/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@KotysaMarker
class ColumnDsl<T : Any> internal constructor(
        private val init: ColumnDsl<T>.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : TableColumnPropertyProvider<T> {

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

    override fun get(property: KProperty1<T, String>) = NotNullStringColumnProperty(property)

    override fun get(property: KProperty1<T, LocalDateTime>) = NotNullLocalDateTimeColumnProperty(property)

    override fun get(property: KProperty1<T, LocalDate>) = NotNullLocalDateColumnProperty(property)

    override fun get(property: KProperty1<T, Instant>) = NotNullInstantColumnProperty(property)

    override fun get(property: KProperty1<T, String?>): NullableStringColumnProperty<T> {
        checkNullableProperty(property)
        return NullableStringColumnProperty(property)
    }

    override fun get(property: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateTimeColumnProperty(property)
    }

    override fun get(property: KProperty1<T, LocalDate?>): NullableLocalDateColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateColumnProperty(property)
    }

    override fun get(property: KProperty1<T, Instant?>): NullableInstantColumnProperty<T> {
        checkNullableProperty(property)
        return NullableInstantColumnProperty(property)
    }

    private fun checkNullableProperty(property: KProperty1<*, *>) {
        require(property.returnType.isMarkedNullable) { "\"${property.name}\" is not a nullable property" }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): Column<T, *> {
        val columnBuilder = init(this) as AbstractColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityProperty.name
        }
        return columnBuilder.build()
    }
}
