/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KProperty1

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
    internal abstract val property: KProperty1<T, *>
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
        override val property: KProperty1<T, String>
) : StringColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableStringColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, String?>
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
        override val property: KProperty1<T, LocalDateTime>
) : LocalDateTimeColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, LocalDateTime?>
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
        override val property: KProperty1<T, LocalDate>
) : LocalDateColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalDateColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, LocalDate?>
) : LocalDateColumnProperty<T>(), NullableColumnProperty

// Instant
/**
 * @author Fred Montariol
 */
abstract class InstantColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullInstantColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, Instant>
) : InstantColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableInstantColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, Instant?>
) : InstantColumnProperty<T>(), NullableColumnProperty
