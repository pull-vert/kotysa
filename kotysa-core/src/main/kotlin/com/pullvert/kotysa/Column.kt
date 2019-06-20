/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * One database Table's Column model mapped by entity's [entityGetter]
 * @author Fred Montariol
 */
interface Column<T : Any, U> {
    var table: Table<T>
    val entityGetter: (T) -> U
    val name: String
    val sqlType: SqlType
    val isPrimaryKey: Boolean
    val isNullable: Boolean
    val defaultValue: Any?
}

/**
 * @author Fred Montariol
 */
internal interface ColumnNotNull<T : Any, U> : Column<T, U> {
    override val isNullable: Boolean
        get() = false
}

/**
 * @author Fred Montariol
 */
internal interface ColumnNullable<T : Any, U> : Column<T, U> {
    override val isNullable: Boolean
        get() = true

    override val defaultValue: Nothing?
        get() = null

    override val isPrimaryKey: Boolean
        get() = false
}

/**
 * @author Fred Montariol
 */
internal abstract class AbstractColumn<T : Any, U> : Column<T, U> {

    override lateinit var table: Table<T>
}

/**
 * @author Fred Montariol
 */
internal interface VarcharColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class VarcharColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class VarcharColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface TimestampColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface TimestampWithTimeZoneColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampWithTimeZoneColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), TimestampWithTimeZoneColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampWithTimeZoneColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), TimestampWithTimeZoneColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface DateColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class DateColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), DateColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class DateColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), DateColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface DateTimeColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class DateTimeColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), DateTimeColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class DateTimeColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), DateTimeColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface TimeColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class TimeColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), TimeColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TimeColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), TimeColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface Time9Column<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class Time9ColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), Time9Column<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class Time9ColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), Time9Column<T, U>, ColumnNullable<T, U>


/**
 * @author Fred Montariol
 */
internal class BooleanColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val defaultValue: U?,
        override val isPrimaryKey: Boolean = false
) : AbstractColumn<T, U>(), ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal interface UuidColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class UuidColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), UuidColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class UuidColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), UuidColumn<T, U>, ColumnNullable<T, U>
