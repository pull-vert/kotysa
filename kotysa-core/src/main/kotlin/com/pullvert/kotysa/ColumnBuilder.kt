/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KProperty1

interface ColumnBuilder<T : ColumnBuilder<T>> {
    fun name(columnName: String): T
}

interface ColumnNotNullBuilder<T : ColumnNotNullBuilder<T, U>, U> : ColumnBuilder<T> {
    val primaryKey: T

    fun setDefaultValue(defaultValue: U): T
}

interface ColumnNullableBuilder<T : ColumnNullableBuilder<T>> : ColumnBuilder<T>

@Suppress("UNCHECKED_CAST")
internal abstract class AbstractColumnBuilder<T : ColumnBuilder<T>, U : Any> : ColumnBuilder<T> {

    protected var isPK: Boolean = false
    protected abstract val sqlType: SqlType
    internal abstract val entityProperty: KProperty1<*, *>
    internal lateinit var columnName: String

    internal val columnNameInitialized
        get() = ::columnName.isInitialized

    override fun name(columnName: String): T {
        this.columnName = columnName
        return this as T
    }

    protected val isPrimaryKey: T
        get() {
            isPK = true
            return this as T
        }

    internal abstract fun build(): Column<U, *>
}

interface VarcharColumnBuilder<T : VarcharColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractVarcharColumnBuilder<T : VarcharColumnBuilder<T>, U : Any> :
        AbstractColumnBuilder<T, U>(), VarcharColumnBuilder<T> {
    override val sqlType = SqlType.VARCHAR
}

interface VarcharColumnBuilderNotNull<U>
    : VarcharColumnBuilder<VarcharColumnBuilderNotNull<U>>, ColumnNotNullBuilder<VarcharColumnBuilderNotNull<U>, U>

internal class VarcharColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractVarcharColumnBuilder<VarcharColumnBuilderNotNull<U>, T>(), VarcharColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): VarcharColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = VarcharColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface VarcharColumnBuilderNullable :
        VarcharColumnBuilder<VarcharColumnBuilderNullable>, ColumnNullableBuilder<VarcharColumnBuilderNullable>

internal class VarcharColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractVarcharColumnBuilder<VarcharColumnBuilderNullable, T>(), VarcharColumnBuilderNullable {
    override fun build() = VarcharColumnNullable(entityProperty, columnName, sqlType)
}

interface TimestampColumnBuilder<T : TimestampColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimestampColumnBuilder<T : TimestampColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimestampColumnBuilder<T> {
    override val sqlType = SqlType.TIMESTAMP
}

interface TimestampColumnBuilderNotNull<U>
    : TimestampColumnBuilder<TimestampColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimestampColumnBuilderNotNull<U>, U>

internal class TimestampColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimestampColumnBuilder<TimestampColumnBuilderNotNull<U>, T>(), TimestampColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimestampColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface TimestampColumnBuilderNullable : TimestampColumnBuilder<TimestampColumnBuilderNullable>, ColumnNullableBuilder<TimestampColumnBuilderNullable>

internal class TimestampColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimestampColumnBuilder<TimestampColumnBuilderNullable, T>(), TimestampColumnBuilderNullable {
    override fun build() = TimestampColumnNullable(entityProperty, columnName, sqlType)
}

interface DateColumnBuilder<T : DateColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractDateColumnBuilder<T : DateColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), DateColumnBuilder<T> {
    override val sqlType = SqlType.DATE
}

interface DateColumnBuilderNotNull<U>
    : DateColumnBuilder<DateColumnBuilderNotNull<U>>, ColumnNotNullBuilder<DateColumnBuilderNotNull<U>, U>

internal class DateColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractDateColumnBuilder<DateColumnBuilderNotNull<U>, T>(), DateColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = DateColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface DateColumnBuilderNullable : DateColumnBuilder<DateColumnBuilderNullable>, ColumnNullableBuilder<DateColumnBuilderNullable>

internal class DateColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractDateColumnBuilder<DateColumnBuilderNullable, T>(), DateColumnBuilderNullable {
    override fun build() = DateColumnNullable(entityProperty, columnName, sqlType)
}

interface DateTimeColumnBuilder<T : DateTimeColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractDateTimeColumnBuilder<T : DateTimeColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), DateTimeColumnBuilder<T> {
    override val sqlType = SqlType.DATE_TIME
}

interface DateTimeColumnBuilderNotNull<U>
    : DateTimeColumnBuilder<DateTimeColumnBuilderNotNull<U>>, ColumnNotNullBuilder<DateTimeColumnBuilderNotNull<U>, U>

internal class DateTimeColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractDateTimeColumnBuilder<DateTimeColumnBuilderNotNull<U>, T>(), DateTimeColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateTimeColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = DateTimeColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface DateTimeColumnBuilderNullable : DateTimeColumnBuilder<DateTimeColumnBuilderNullable>, ColumnNullableBuilder<DateTimeColumnBuilderNullable>

internal class DateTimeColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractDateTimeColumnBuilder<DateTimeColumnBuilderNullable, T>(), DateTimeColumnBuilderNullable {
    override fun build() = DateTimeColumnNullable(entityProperty, columnName, sqlType)
}

interface TimeColumnBuilder<T : TimeColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimeColumnBuilder<T : TimeColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimeColumnBuilder<T> {
    override val sqlType = SqlType.TIME
}

interface TimeColumnBuilderNotNull<U>
    : TimeColumnBuilder<TimeColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimeColumnBuilderNotNull<U>, U>

internal class TimeColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimeColumnBuilder<TimeColumnBuilderNotNull<U>, T>(), TimeColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimeColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimeColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
}

interface TimeColumnBuilderNullable : TimeColumnBuilder<TimeColumnBuilderNullable>, ColumnNullableBuilder<TimeColumnBuilderNullable>

internal class TimeColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>
) : AbstractTimeColumnBuilder<TimeColumnBuilderNullable, T>(), TimeColumnBuilderNullable {
    override fun build() = TimeColumnNullable(entityProperty, columnName, sqlType)
}
