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
    internal abstract val alias: String?
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
        override val property: KProperty1<T, String>,
        override val alias: String?
) : StringColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableStringColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, String?>,
        override val alias: String?
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
        override val property: KProperty1<T, LocalDateTime>,
        override val alias: String?
) : LocalDateTimeColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, LocalDateTime?>,
        override val alias: String?
) : LocalDateTimeColumnProperty<T>(), NullableColumnProperty

// Date
/**
 * @author Fred Montariol
 */
abstract class DateColumnProperty<T : Any> : AbstractColumnProperty<T>()

/**
 * @author Fred Montariol
 */
class NotNullDateColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, Date>,
        override val alias: String?
) : DateColumnProperty<T>(), NotNullColumnProperty

/**
 * @author Fred Montariol
 */
class NullableDateColumnProperty<T : Any> internal constructor(
        override val property: KProperty1<T, Date?>,
        override val alias: String?
) : DateColumnProperty<T>(), NullableColumnProperty
