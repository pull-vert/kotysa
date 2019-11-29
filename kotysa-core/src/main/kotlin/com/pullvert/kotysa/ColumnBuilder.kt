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
    protected var defaultValue: V? = null

    @Suppress("UNCHECKED_CAST")
    fun setDefaultValue(defaultValue: V): T {
        this.defaultValue = defaultValue
        return this as T
    }
}

@Suppress("UNCHECKED_CAST")
abstract class ColumnNotNullBuilder<T : ColumnNotNullBuilder<T, U, V>, U : Any, V> : ColumnBuilder<T, U>() {
    protected var defaultValue: V? = null

    fun setDefaultValue(defaultValue: V): T {
        this.defaultValue = defaultValue
        return this as T
    }

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

    override fun build() =
            VarcharColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class VarcharColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<VarcharColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.VARCHAR

    override fun build() = VarcharColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class TextColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<TextColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TEXT

    override fun build() =
            TextColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class TextColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<TextColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.TEXT

    override fun build() = TextColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class TimestampColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<TimestampColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.TIMESTAMP

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

    override fun build() =
            BooleanColumnNotNull(entityGetter, columnName, sqlType, defaultValue, fkClass, fkName)
}

class UuidColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<UuidColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.UUID

    override fun build() =
            UuidColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
}

class UuidColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<UuidColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.UUID

    override fun build() = UuidColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
}

class IntegerColumnBuilderNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNotNullBuilder<IntegerColumnBuilderNotNull<T, U>, T, U>() {
    override val sqlType = SqlType.INTEGER

    private var isAutoIncrement: Boolean = false

    fun autoIncrement(): IntegerColumnBuilderNotNull<T, U> {
        isAutoIncrement = true
        return this
    }

    override fun build() =
            IntegerColumnNotNull(entityGetter, columnName, sqlType, isPK, isAutoIncrement, pkName, defaultValue, fkClass, fkName)
}

class IntegerColumnBuilderNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U
) : ColumnNullableBuilder<IntegerColumnBuilderNullable<T, U>, T>() {
    override val sqlType = SqlType.INTEGER

    private var isAutoIncrement: Boolean = false

    fun autoIncrement(): IntegerColumnBuilderNullable<T, U> {
        isAutoIncrement = true
        return this
    }

    override fun build() = IntegerColumnNullable(entityGetter, columnName, sqlType, isAutoIncrement, fkClass, fkName)
}
