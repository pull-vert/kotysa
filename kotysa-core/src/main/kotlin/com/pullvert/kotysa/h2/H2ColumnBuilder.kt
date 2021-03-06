/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*

public class TimestampWithTimeZoneColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<TimestampWithTimeZoneColumnBuilderNotNull<T, U>, T, U>(SqlType.TIMESTAMP_WITH_TIME_ZONE, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        TimestampWithTimeZoneColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

public class TimestampWithTimeZoneColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<TimestampWithTimeZoneColumnBuilderNullable<T, U>, T, U>(SqlType.TIMESTAMP_WITH_TIME_ZONE, entityGetter) {
    override fun build() = with(props) {
        TimestampWithTimeZoneColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): TimestampWithTimeZoneColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return TimestampWithTimeZoneColumnBuilderNotNull(entityGetter, props)
    }
}

public class Time9ColumnBuilderNotNull<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNotNullBuilder<Time9ColumnBuilderNotNull<T, U>, T, U>(SqlType.TIME9, entityGetter) {

    internal constructor(entityGetter: (T) -> U?, props: ColumnBuilderProps<T, U>) : this(entityGetter) {
        this.props = props
    }

    override fun build() = with(props) {
        Time9ColumnNotNull(entityGetter, columnName, sqlType, isPK, pkName, defaultValue, fkClass, fkName)
    }
}

public class Time9ColumnBuilderNullable<T : Any, U : Any> internal constructor(
        entityGetter: (T) -> U?
) : ColumnNullableBuilder<Time9ColumnBuilderNullable<T, U>, T, U>(SqlType.TIME9, entityGetter) {
    override fun build() = with(props) {
        Time9ColumnNullable(entityGetter, columnName, sqlType, fkClass, fkName)
    }

    override fun defaultValue(defaultValue: U): Time9ColumnBuilderNotNull<T, U> {
        props.defaultValue = defaultValue
        return Time9ColumnBuilderNotNull(entityGetter, props)
    }
}
