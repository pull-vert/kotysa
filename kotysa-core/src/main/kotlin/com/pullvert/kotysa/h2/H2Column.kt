/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.*

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
