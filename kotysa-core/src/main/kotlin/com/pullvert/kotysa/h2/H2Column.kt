/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*
import kotlin.reflect.KClass

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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimestampWithTimeZoneColumn<T, U>, ColumnNotNull<T, U>, NoAutoIncrement<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampWithTimeZoneColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), TimestampWithTimeZoneColumn<T, U>, ColumnNullable<T, U>, NoAutoIncrement<T, U>

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
        override val pkName: String?,
        override val defaultValue: U?,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), Time9Column<T, U>, ColumnNotNull<T, U>, NoAutoIncrement<T, U>

/**
 * @author Fred Montariol
 */
internal class Time9ColumnNullable<T : Any, U> internal constructor(
        override val entityGetter: (T) -> U,
        override val name: String,
        override val sqlType: SqlType,
        override val fkClass: KClass<*>?,
        override val fkName: String?
) : AbstractColumn<T, U>(), Time9Column<T, U>, ColumnNullable<T, U>, NoAutoIncrement<T, U>
