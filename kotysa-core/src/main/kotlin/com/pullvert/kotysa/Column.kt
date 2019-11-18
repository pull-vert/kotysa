/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * One database Table's Column model mapped by entity's [entityGetter]
 * @author Fred Montariol
 */
interface Column<T : Any, U> {
    /**
     * Table this column is in
     */
    var table: Table<T>
    val entityGetter: (T) -> U
    val name: String
    val sqlType: SqlType
    val isPrimaryKey: Boolean
    val pkName: String?
    val isNullable: Boolean
    val defaultValue: U?
    val fkClass: KClass<*>?
    var fkColumn: Column<*, *>?
    val fkName: String?
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

    override val defaultValue: U?
        get() = null

    override val isPrimaryKey: Boolean
        get() = false

    override val pkName: String?
        get() = null
}

/**
 * @author Fred Montariol
 */
internal abstract class AbstractColumn<T : Any, U> : Column<T, U> {
    override lateinit var table: Table<T>
    override var fkColumn: Column<*, *>? = null
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
        override val pkName: String?,
        override val defaultValue: U,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class VarcharColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface TextColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class TextColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val pkName: String?,
        override val defaultValue: U,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TextColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TextColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TextColumn<T, U>, ColumnNullable<T, U>

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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNullable<T, U>

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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), DateColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class DateColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override var fkClass: KClass<*>?,
        override var fkName: String?
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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), DateTimeColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class DateTimeColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimeColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class TimeColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimeColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal class BooleanColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), ColumnNotNull<T, U> {

    override val isPrimaryKey: Boolean = false

    override val pkName: String? = null
}

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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), UuidColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class UuidColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), UuidColumn<T, U>, ColumnNullable<T, U>

/**
 * @author Fred Montariol
 */
internal interface IntegerColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class IntegerColumnNotNull<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), IntegerColumn<T, U>, ColumnNotNull<T, U>

/**
 * @author Fred Montariol
 */
internal class IntegerColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), IntegerColumn<T, U>, ColumnNullable<T, U>
