/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*
import kotlin.reflect.KProperty1

interface TimestampWithTimeZoneColumnBuilder<T : TimestampWithTimeZoneColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimestampWithTimeZoneColumnBuilder<T : TimestampWithTimeZoneColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimestampWithTimeZoneColumnBuilder<T> {
    override val sqlType = SqlType.TIMESTAMP_WITH_TIME_ZONE
}

interface TimestampWithTimeZoneColumnBuilderNotNull<U>
    : TimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>, U>

internal class TimestampWithTimeZoneColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>, T>(), TimestampWithTimeZoneColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampWithTimeZoneColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimestampWithTimeZoneColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface TimestampWithTimeZoneColumnBuilderNullable : TimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNullable>, ColumnNullableBuilder<TimestampWithTimeZoneColumnBuilderNullable>

internal class TimestampWithTimeZoneColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNullable, T>(), TimestampWithTimeZoneColumnBuilderNullable {
    override fun build() = TimestampWithTimeZoneColumnNullable(entityProperty, columnName, sqlType)
}

interface Time9ColumnBuilder<T : Time9ColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTime9ColumnBuilder<T : Time9ColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), Time9ColumnBuilder<T> {
    override val sqlType = SqlType.TIME9
}

interface Time9ColumnBuilderNotNull<U>
    : Time9ColumnBuilder<Time9ColumnBuilderNotNull<U>>, ColumnNotNullBuilder<Time9ColumnBuilderNotNull<U>, U>

internal class Time9ColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTime9ColumnBuilder<Time9ColumnBuilderNotNull<U>, T>(), Time9ColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): Time9ColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = Time9ColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface Time9ColumnBuilderNullable : Time9ColumnBuilder<Time9ColumnBuilderNullable>, ColumnNullableBuilder<Time9ColumnBuilderNullable>

internal class Time9ColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTime9ColumnBuilder<Time9ColumnBuilderNullable, T>(), Time9ColumnBuilderNullable {
    override fun build() = Time9ColumnNullable(entityProperty, columnName, sqlType)
}
