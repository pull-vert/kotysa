/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@KotysaMarker
abstract class ColumnDsl<T : Any, U : ColumnDsl<T, U>> internal constructor(
        private val init: U.(TableColumnPropertyProvider<T>) -> ColumnBuilder<*>
) : TableColumnPropertyProvider<T> {

    override fun get(property: KProperty1<T, String>) = NotNullStringColumnProperty(property)

    override fun get(property: KProperty1<T, String?>): NullableStringColumnProperty<T> {
        checkNullableProperty(property)
        return NullableStringColumnProperty(property)
    }

    override fun get(property: KProperty1<T, LocalDateTime>) = NotNullLocalDateTimeColumnProperty(property)

    override fun get(property: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateTimeColumnProperty(property)
    }

    override fun get(property: KProperty1<T, LocalDate>) = NotNullLocalDateColumnProperty(property)

    override fun get(property: KProperty1<T, LocalDate?>): NullableLocalDateColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateColumnProperty(property)
    }

    override fun get(property: KProperty1<T, Instant>) = NotNullInstantColumnProperty(property)

    override fun get(property: KProperty1<T, Instant?>): NullableInstantColumnProperty<T> {
        checkNullableProperty(property)
        return NullableInstantColumnProperty(property)
    }

    override fun get(property: KProperty1<T, LocalTime>) = NotNullLocalTimeColumnProperty(property)

    override fun get(property: KProperty1<T, LocalTime?>): NullableLocalTimeColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalTimeColumnProperty(property)
    }

    private fun checkNullableProperty(property: KProperty1<*, *>) {
        require(property.returnType.isMarkedNullable) { "\"${property.name}\" is not a nullable property" }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(initialize: U): Column<T, *> {
        val columnBuilder = init(initialize, initialize) as AbstractColumnBuilder<*, T>
        if (!columnBuilder.columnNameInitialized) {
            columnBuilder.columnName = columnBuilder.entityProperty.name
        }
        return columnBuilder.build()
    }
}
