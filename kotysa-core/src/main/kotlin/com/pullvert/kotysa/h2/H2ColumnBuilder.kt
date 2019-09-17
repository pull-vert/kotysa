/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.ColumnNotNullBuilder
import com.pullvert.kotysa.ColumnNullableBuilder
import com.pullvert.kotysa.SqlType

class TimestampWithTimeZoneColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<TimestampWithTimeZoneColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TIMESTAMP_WITH_TIME_ZONE

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampWithTimeZoneColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimestampWithTimeZoneColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue, fkClass)
}

class TimestampWithTimeZoneColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<TimestampWithTimeZoneColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.TIMESTAMP_WITH_TIME_ZONE

    override fun build() = TimestampWithTimeZoneColumnNullable(entityGetter, columnName, sqlType, fkClass)
}

class Time9ColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<Time9ColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TIME9

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): Time9ColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = Time9ColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue, fkClass)
}

class Time9ColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<Time9ColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.TIME9

    override fun build() = Time9ColumnNullable(entityGetter, columnName, sqlType, fkClass)
}
