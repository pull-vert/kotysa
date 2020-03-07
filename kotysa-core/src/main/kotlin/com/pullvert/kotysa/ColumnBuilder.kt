/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

interface ColumnBuilderProps<T : Any, U : Any> {
    var isPK: Boolean
    var pkName: String?
    val sqlType: SqlType
    val entityGetter: (T) -> U?
    var columnName: String
    val isColumnNameInitialized: Boolean
    var fkClass: KClass<*>?
    var fkName: String?
    var defaultValue: U?
    var isAutoIncrement: Boolean
}

private class ColumnBuilderPropsImpl<T : Any, U : Any> internal constructor(
        override val sqlType: SqlType,
        override val entityGetter: (T) -> U?
) : ColumnBuilderProps<T, U> {
    override lateinit var columnName: String
    override val isColumnNameInitialized get() = ::columnName.isInitialized
    override var isPK: Boolean = false
    override var pkName: String? = null
    override var fkClass: KClass<*>? = null
    override var fkName: String? = null
    override var defaultValue: U? = null
    override var isAutoIncrement: Boolean = false
}

@Suppress("UNCHECKED_CAST")
abstract class ColumnBuilder<T : ColumnBuilder<T, U, V>, U : Any, V : Any> internal constructor(
        sqlType: SqlType,
        val entityGetter: (U) -> V?
) {

    internal var props: ColumnBuilderProps<U, V> = ColumnBuilderPropsImpl(sqlType, entityGetter)

    internal val isColumnNameInitialized get() = props.isColumnNameInitialized

    fun name(columnName: String): T {
        props.columnName = columnName
        return this as T
    }

    inline fun <reified V : Any> foreignKey(fkName: String? = null) = foreignKey(V::class, fkName)

    @PublishedApi
    internal fun <V : Any> foreignKey(fkClass: KClass<V>, fkName: String?): T {
        props.fkClass = fkClass
        props.fkName = fkName
        return this as T
    }

    internal abstract fun build(): Column<U, *>
}

abstract class ColumnNotNullNoPkBuilder<T : ColumnNotNullNoPkBuilder<T, U, V>, U : Any, V : Any> internal constructor(
        sqlType: SqlType,
        entityGetter: (U) -> V?
) : ColumnBuilder<T, U, V>(sqlType, entityGetter)

@Suppress("UNCHECKED_CAST")
abstract class ColumnNotNullBuilder<T : ColumnNotNullBuilder<T, U, V>, U : Any, V : Any> internal constructor(
        sqlType: SqlType,
        entityGetter: (U) -> V?
) : ColumnBuilder<T, U, V>(sqlType, entityGetter) {

    fun primaryKey(pkName: String? = null): T {
        props.isPK = true
        props.pkName = pkName
        return this as T
    }
}

abstract class ColumnNullableBuilder<T : ColumnNullableBuilder<T, U, V>, U : Any, V : Any> internal constructor(
        sqlType: SqlType,
        entityGetter: (U) -> V?
) : ColumnBuilder<T, U, V>(sqlType, entityGetter) {

    abstract fun defaultValue(defaultValue: V): ColumnNotNullBuilder<*, U, V>
}

class VarcharColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<VarcharColumnBuilderNotNull<T, U>, T, U>(SqlType.VARCHAR, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        VarcharColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class VarcharColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<VarcharColumnBuilderNullable<T, U>, T, U>(SqlType.VARCHAR, entityGetter) {
    override fun build() = with(props) {
        VarcharColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): VarcharColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return VarcharColumnBuilderNotNull(entityGetter, props)
    }
}

class TextColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<TextColumnBuilderNotNull<T, U>, T, U>(SqlType.TEXT, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        TextColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class TextColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<TextColumnBuilderNullable<T, U>, T, U>(SqlType.TEXT, entityGetter) {
    override fun build() = with(props) {
        TextColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): TextColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return TextColumnBuilderNotNull(entityGetter, props)
    }
}

class TimestampColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<TimestampColumnBuilderNotNull<T, U>, T, U>(SqlType.TIMESTAMP, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        TimestampColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class TimestampColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<TimestampColumnBuilderNullable<T, U>, T, U>(SqlType.TIMESTAMP, entityGetter) {
    override fun build() = with(props) {
        TimestampColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): TimestampColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return TimestampColumnBuilderNotNull(entityGetter, props)
    }
}

class DateColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<DateColumnBuilderNotNull<T, U>, T, U>(SqlType.DATE, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        DateColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class DateColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<DateColumnBuilderNullable<T, U>, T, U>(SqlType.DATE, entityGetter) {
    override fun build() = with(props) {
        DateColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): DateColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return DateColumnBuilderNotNull(entityGetter, props)
    }
}

class DateTimeColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<DateTimeColumnBuilderNotNull<T, U>, T, U>(SqlType.DATE_TIME, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        DateTimeColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class DateTimeColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<DateTimeColumnBuilderNullable<T, U>, T, U>(SqlType.DATE_TIME, entityGetter) {
    override fun build() = with(props) {
        DateTimeColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): DateTimeColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return DateTimeColumnBuilderNotNull(entityGetter, props)
    }
}

class TimeColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<TimeColumnBuilderNotNull<T, U>, T, U>(SqlType.TIME, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        TimeColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class TimeColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<TimeColumnBuilderNullable<T, U>, T, U>(SqlType.TIME, entityGetter) {
    override fun build() = with(props) {
        TimeColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): TimeColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return TimeColumnBuilderNotNull(entityGetter, props)
    }
}

class BooleanColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U
) : ColumnNotNullNoPkBuilder<BooleanColumnBuilderNotNull<T, U>, T, U>(SqlType.BOOLEAN, entityGetter) {
    override fun build() = with(props) {
        BooleanColumnNotNull(entityGetter, columnName, sqlType, defaultValue, fkClass, fkName)
    }
}

class UuidColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<UuidColumnBuilderNotNull<T, U>, T, U>(SqlType.UUID, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        UuidColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

class UuidColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<UuidColumnBuilderNullable<T, U>, T, U>(SqlType.UUID, entityGetter) {
    override fun build() = with(props) {
        UuidColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): UuidColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return UuidColumnBuilderNotNull(entityGetter, props)
    }
}

class IntegerColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<IntegerColumnBuilderNotNull<T, U>, T, U>(SqlType.INTEGER, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    fun autoIncrement(): IntegerColumnBuilderNotNull<T, U> {
        props.isAutoIncrement = true
        return this
    }

    override fun build() = with(props) {
        IntegerColumnNotNull(entityGetter, columnName, sqlType, isPK, isAutoIncrement, pkName, defaultValue, fkClass, fkName)
    }
}

class IntegerColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<IntegerColumnBuilderNullable<T, U>, T, U>(SqlType.INTEGER, entityGetter) {
    fun autoIncrement(): IntegerColumnBuilderNotNull<T, U> {
        props.isAutoIncrement = true
        return IntegerColumnBuilderNotNull(entityGetter, props)
    }

    override fun build() = with(props) {
        IntegerColumnNullable(entityGetter, columnName, sqlType, isAutoIncrement, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): IntegerColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return IntegerColumnBuilderNotNull(entityGetter, props)
    }
}

class SerialColumnBuilder<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<SerialColumnBuilder<T, U>, T, U>(SqlType.SERIAL, entityGetter) {
    override fun build() = with(props) {
        SerialColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}
