/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
interface ColumnProperty

/**
 * @author Fred Montariol
 */
interface NotNullColumnProperty : ColumnProperty

/**
 * @author Fred Montariol
 */
interface NullableColumnProperty : ColumnProperty

/**
 * @author Fred Montariol
 */
abstract class AbstractColumnProperty<T : Any> : ColumnProperty {
    internal abstract val getter: (T) -> Any?
}

// String
/**
 * @author Fred Montariol
 */
abstract class StringColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullStringColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> String
) : StringColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableStringColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> String?
) : StringColumnProperty<T>(), NullableColumnProperty

// LocalDateTime
/**
 * @author Fred Montariol
 */
abstract class LocalDateTimeColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullLocalDateTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalDateTime
) : LocalDateTimeColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalDateTime?
) : LocalDateTimeColumnProperty<T>(), NullableColumnProperty

// LocalDate
/**
 * @author Fred Montariol
 */
abstract class LocalDateColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullLocalDateColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalDate
) : LocalDateColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalDateColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalDate?
) : LocalDateColumnProperty<T>(), NullableColumnProperty

// OffsetDateTime
/**
 * @author Fred Montariol
 */
abstract class OffsetDateTimeColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullOffsetDateTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> OffsetDateTime
) : OffsetDateTimeColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableOffsetDateTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> OffsetDateTime?
) : OffsetDateTimeColumnProperty<T>(), NullableColumnProperty

// LocalTime
/**
 * @author Fred Montariol
 */
abstract class LocalTimeColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullLocalTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalTime
) : LocalTimeColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalTimeColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> LocalTime?
) : LocalTimeColumnProperty<T>(), NullableColumnProperty

// Boolean
/**
 * @author Fred Montariol
 */
class NotNullBooleanColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> Boolean
) : AbstractColumnProperty<T>(), NotNullColumnProperty

// UUID
/**
 * @author Fred Montariol
 */
abstract class UuidColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullUuidColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> UUID
) : UuidColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableUuidColumnProperty<T : Any> internal constructor(
        override val getter: (T) -> UUID?
) : UuidColumnProperty<T>(), NullableColumnProperty
