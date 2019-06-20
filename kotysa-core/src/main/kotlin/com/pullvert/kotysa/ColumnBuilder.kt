/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

interface ColumnBuilder<T : ColumnBuilder<T>> {
    fun name(columnName: String): T
}

interface ColumnNotNullNoPkBuilder<T : ColumnNotNullNoPkBuilder<T, U>, U> : ColumnBuilder<T> {
    fun setDefaultValue(defaultValue: U): T
}

interface ColumnNotNullBuilder<T : ColumnNotNullBuilder<T, U>, U> : ColumnNotNullNoPkBuilder<T, U> {
    val primaryKey: T

    override fun setDefaultValue(defaultValue: U): T
}

interface ColumnNullableBuilder<T : ColumnNullableBuilder<T>> : ColumnBuilder<T>

@Suppress("UNCHECKED_CAST")
internal abstract class AbstractColumnBuilder<T : ColumnBuilder<T>, U : Any> : ColumnBuilder<T> {

    protected var isPK: Boolean = false
    protected abstract val sqlType: SqlType
    internal abstract val entityGetter: (U) -> Any?
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
        override val entityGetter: (T) -> U
) : AbstractVarcharColumnBuilder<VarcharColumnBuilderNotNull<U>, T>(), VarcharColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): VarcharColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = VarcharColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface VarcharColumnBuilderNullable :
        VarcharColumnBuilder<VarcharColumnBuilderNullable>, ColumnNullableBuilder<VarcharColumnBuilderNullable>

internal class VarcharColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractVarcharColumnBuilder<VarcharColumnBuilderNullable, T>(), VarcharColumnBuilderNullable {
    override fun build() = VarcharColumnNullable(entityGetter, columnName, sqlType)
}

interface TimestampColumnBuilder<T : TimestampColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimestampColumnBuilder<T : TimestampColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimestampColumnBuilder<T> {
    override val sqlType = SqlType.TIMESTAMP
}

interface TimestampColumnBuilderNotNull<U>
    : TimestampColumnBuilder<TimestampColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimestampColumnBuilderNotNull<U>, U>

internal class TimestampColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimestampColumnBuilder<TimestampColumnBuilderNotNull<U>, T>(), TimestampColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimestampColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface TimestampColumnBuilderNullable : TimestampColumnBuilder<TimestampColumnBuilderNullable>, ColumnNullableBuilder<TimestampColumnBuilderNullable>

internal class TimestampColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimestampColumnBuilder<TimestampColumnBuilderNullable, T>(), TimestampColumnBuilderNullable {
    override fun build() = TimestampColumnNullable(entityGetter, columnName, sqlType)
}

interface TimestampWithTimeZoneColumnBuilder<T : TimestampWithTimeZoneColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimestampWithTimeZoneColumnBuilder<T : TimestampWithTimeZoneColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimestampWithTimeZoneColumnBuilder<T> {
    override val sqlType = SqlType.TIMESTAMP_WITH_TIME_ZONE
}

interface TimestampWithTimeZoneColumnBuilderNotNull<U>
    : TimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>, U>

internal class TimestampWithTimeZoneColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNotNull<U>, T>(), TimestampWithTimeZoneColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampWithTimeZoneColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimestampWithTimeZoneColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface TimestampWithTimeZoneColumnBuilderNullable : TimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNullable>, ColumnNullableBuilder<TimestampWithTimeZoneColumnBuilderNullable>

internal class TimestampWithTimeZoneColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimestampWithTimeZoneColumnBuilder<TimestampWithTimeZoneColumnBuilderNullable, T>(), TimestampWithTimeZoneColumnBuilderNullable {
    override fun build() = TimestampWithTimeZoneColumnNullable(entityGetter, columnName, sqlType)
}

interface DateColumnBuilder<T : DateColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractDateColumnBuilder<T : DateColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), DateColumnBuilder<T> {
    override val sqlType = SqlType.DATE
}

interface DateColumnBuilderNotNull<U>
    : DateColumnBuilder<DateColumnBuilderNotNull<U>>, ColumnNotNullBuilder<DateColumnBuilderNotNull<U>, U>

internal class DateColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractDateColumnBuilder<DateColumnBuilderNotNull<U>, T>(), DateColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = DateColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface DateColumnBuilderNullable : DateColumnBuilder<DateColumnBuilderNullable>, ColumnNullableBuilder<DateColumnBuilderNullable>

internal class DateColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractDateColumnBuilder<DateColumnBuilderNullable, T>(), DateColumnBuilderNullable {
    override fun build() = DateColumnNullable(entityGetter, columnName, sqlType)
}

interface DateTimeColumnBuilder<T : DateTimeColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractDateTimeColumnBuilder<T : DateTimeColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), DateTimeColumnBuilder<T> {
    override val sqlType = SqlType.DATE_TIME
}

interface DateTimeColumnBuilderNotNull<U>
    : DateTimeColumnBuilder<DateTimeColumnBuilderNotNull<U>>, ColumnNotNullBuilder<DateTimeColumnBuilderNotNull<U>, U>

internal class DateTimeColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractDateTimeColumnBuilder<DateTimeColumnBuilderNotNull<U>, T>(), DateTimeColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateTimeColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = DateTimeColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface DateTimeColumnBuilderNullable : DateTimeColumnBuilder<DateTimeColumnBuilderNullable>, ColumnNullableBuilder<DateTimeColumnBuilderNullable>

internal class DateTimeColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractDateTimeColumnBuilder<DateTimeColumnBuilderNullable, T>(), DateTimeColumnBuilderNullable {
    override fun build() = DateTimeColumnNullable(entityGetter, columnName, sqlType)
}

interface TimeColumnBuilder<T : TimeColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTimeColumnBuilder<T : TimeColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), TimeColumnBuilder<T> {
    override val sqlType = SqlType.TIME
}

interface TimeColumnBuilderNotNull<U>
    : TimeColumnBuilder<TimeColumnBuilderNotNull<U>>, ColumnNotNullBuilder<TimeColumnBuilderNotNull<U>, U>

internal class TimeColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimeColumnBuilder<TimeColumnBuilderNotNull<U>, T>(), TimeColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimeColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = TimeColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface TimeColumnBuilderNullable : TimeColumnBuilder<TimeColumnBuilderNullable>, ColumnNullableBuilder<TimeColumnBuilderNullable>

internal class TimeColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTimeColumnBuilder<TimeColumnBuilderNullable, T>(), TimeColumnBuilderNullable {
    override fun build() = TimeColumnNullable(entityGetter, columnName, sqlType)
}

interface Time9ColumnBuilder<T : Time9ColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractTime9ColumnBuilder<T : Time9ColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), Time9ColumnBuilder<T> {
    override val sqlType = SqlType.TIME9
}

interface Time9ColumnBuilderNotNull<U>
    : Time9ColumnBuilder<Time9ColumnBuilderNotNull<U>>, ColumnNotNullBuilder<Time9ColumnBuilderNotNull<U>, U>

internal class Time9ColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTime9ColumnBuilder<Time9ColumnBuilderNotNull<U>, T>(), Time9ColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): Time9ColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = Time9ColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface Time9ColumnBuilderNullable : Time9ColumnBuilder<Time9ColumnBuilderNullable>, ColumnNullableBuilder<Time9ColumnBuilderNullable>

internal class Time9ColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractTime9ColumnBuilder<Time9ColumnBuilderNullable, T>(), Time9ColumnBuilderNullable {
    override fun build() = Time9ColumnNullable(entityGetter, columnName, sqlType)
}

interface BooleanColumnBuilderNotNull<U>
    : ColumnNotNullNoPkBuilder<BooleanColumnBuilderNotNull<U>, U>

internal class BooleanColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractColumnBuilder<BooleanColumnBuilderNotNull<U>, T>(), BooleanColumnBuilderNotNull<U> {
    override val sqlType = SqlType.BOOLEAN

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): BooleanColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() = BooleanColumnNotNull(entityGetter, columnName, sqlType, defaultValue)
}

interface UuidColumnBuilder<T : UuidColumnBuilder<T>> : ColumnBuilder<T>

internal abstract class AbstractUuidColumnBuilder<T : UuidColumnBuilder<T>, U : Any> : AbstractColumnBuilder<T, U>(), UuidColumnBuilder<T> {
    override val sqlType = SqlType.UUID
}

interface UuidColumnBuilderNotNull<U>
    : UuidColumnBuilder<UuidColumnBuilderNotNull<U>>, ColumnNotNullBuilder<UuidColumnBuilderNotNull<U>, U>

internal class UuidColumnBuilderNotNullImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractUuidColumnBuilder<UuidColumnBuilderNotNull<U>, T>(), UuidColumnBuilderNotNull<U> {

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): UuidColumnBuilderNotNull<U> {
        this.defaultValue = defaultValue
        return this
    }

    override val primaryKey
        get() = isPrimaryKey

    override fun build() = UuidColumnNotNull(entityGetter, columnName, sqlType, isPK, defaultValue)
}

interface UuidColumnBuilderNullable : UuidColumnBuilder<UuidColumnBuilderNullable>, ColumnNullableBuilder<UuidColumnBuilderNullable>

internal class UuidColumnBuilderNullableImpl<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : AbstractUuidColumnBuilder<UuidColumnBuilderNullable, T>(), UuidColumnBuilderNullable {
    override fun build() = UuidColumnNullable(entityGetter, columnName, sqlType)
}
