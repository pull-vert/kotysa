/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class ColumnBuilder<T : ColumnBuilder<T, U>, U : Any> {

    protected var isPK: Boolean = false
    protected var pkName: String? = null
    protected abstract val sqlType: SqlType
    internal abstract val entityGetter: (U) -> Any?
    internal lateinit var columnName: String
    protected var fkClass: KClass<*>? = null
    protected var fkName: String? = null

    internal val columnNameInitialized
        get() = ::columnName.isInitialized

    fun name(columnName: String): T {
        this.columnName = columnName
        return this as T
    }

    inline fun <reified V : Any> foreignKey(fkName: String? = null) = foreignKey(V::class, fkName)

    @PublishedApi
    internal fun <V : Any> foreignKey(fkClass: KClass<V>, fkName: String?): T {
        this.fkClass = fkClass
        this.fkName = fkName
        return this as T
    }

    internal abstract fun build(): Column<U, *>
}

abstract class ColumnNotNullNoPkBuilder<T : ColumnNotNullNoPkBuilder<T, U, V>, U : Any, V> : ColumnBuilder<T, U>() {
    abstract fun setDefaultValue(defaultValue: V): T
}

abstract class ColumnNotNullBuilder<T : ColumnNotNullBuilder<T, U, V>, U : Any, V> : ColumnNotNullNoPkBuilder<T, U, V>() {

    abstract override fun setDefaultValue(defaultValue: V): T

    @Suppress("UNCHECKED_CAST")
    fun primaryKey(pkName: String? = null): T {
        isPK = true
        this.pkName = pkName
        return this as T
    }
}

abstract class ColumnNullableBuilder<T : ColumnNullableBuilder<T, U>, U : Any> : ColumnBuilder<T, U>()


class VarcharColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<VarcharColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.VARCHAR

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): VarcharColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            VarcharColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class VarcharColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<VarcharColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.VARCHAR

    override fun build() = VarcharColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class TimestampColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<TimestampColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TIMESTAMP

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimestampColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            TimestampColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class TimestampColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<TimestampColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.TIMESTAMP

    override fun build() = TimestampColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class DateColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<DateColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.DATE

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            DateColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class DateColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<DateColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.DATE

    override fun build() = DateColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class DateTimeColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<DateTimeColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.DATE_TIME

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): DateTimeColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            DateTimeColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class DateTimeColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<DateTimeColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.DATE_TIME

    override fun build() = DateTimeColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class TimeColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<TimeColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TIME

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): TimeColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            TimeColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class TimeColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<TimeColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.TIME

    override fun build() = TimeColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class BooleanColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullNoPkBuilder<BooleanColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.BOOLEAN

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): BooleanColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            BooleanColumnNotNull(entityGetter, columnName, sqlType, defaultValue, fkClass, fkName)
}

class UuidColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<UuidColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.UUID

    private var defaultValue: U? = null

    override fun setDefaultValue(defaultValue: U): UuidColumnBuilderNotNull<T, U> {
        this.defaultValue = defaultValue
        return this
    }

    override fun build() =
            UuidColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class UuidColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<UuidColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.UUID

    override fun build() = UuidColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}
