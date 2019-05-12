/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
interface ColumnChoice
/**
 * @author Fred Montariol
 */
interface NotNullColumnChoice : ColumnChoice
/**
 * @author Fred Montariol
 */
interface NullableColumnChoice : ColumnChoice

/**
 * @author Fred Montariol
 */
abstract class AbstractColumnChoice<T : Any> : ColumnChoice {
	internal abstract val property: KProperty1<T, *>
}

// String
/**
 * @author Fred Montariol
 */
abstract class StringColumnChoice<T : Any>: AbstractColumnChoice<T>()
/**
 * @author Fred Montariol
 */
class NotNullStringColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, String>
) : StringColumnChoice<T>(), NotNullColumnChoice
/**
 * @author Fred Montariol
 */
class NullableStringColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, String?>
) : StringColumnChoice<T>(), NullableColumnChoice

// LocalDateTime
/**
 * @author Fred Montariol
 */
abstract class LocalDateTimeColumnChoice<T : Any>: AbstractColumnChoice<T>()
/**
 * @author Fred Montariol
 */
class NotNullLocalDateTimeColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, LocalDateTime>
) : LocalDateTimeColumnChoice<T>(), NotNullColumnChoice
/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, LocalDateTime?>
) : LocalDateTimeColumnChoice<T>(), NullableColumnChoice

// Date
/**
 * @author Fred Montariol
 */
abstract class DateColumnChoice<T : Any>: AbstractColumnChoice<T>()
/**
 * @author Fred Montariol
 */
class NotNullDateColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, Date>
) : DateColumnChoice<T>(), NotNullColumnChoice
/**
 * @author Fred Montariol
 */
class NullableDateColumnChoice<T : Any> internal constructor(
		override val property: KProperty1<T, Date?>
) : DateColumnChoice<T>(), NullableColumnChoice
