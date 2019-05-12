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
interface ValueProvider {

	operator fun get(stringProperty: KProperty1<*, String>): String

	operator fun get(nullableStringProperty: KProperty1<*, String?>, `_`: Nullable = Nullable.TRUE): String?

	operator fun get(localDateTimeProperty: KProperty1<*, LocalDateTime>): LocalDateTime

	operator fun get(nullableLocalDateTimeProperty: KProperty1<*, LocalDateTime?>, `_`: Nullable = Nullable.TRUE): LocalDateTime?

	operator fun get(dateProperty: KProperty1<*, Date>): Date

	operator fun get(nullableDateProperty: KProperty1<*, Date?>, `_`: Nullable = Nullable.TRUE): Date?
}

/**
 * Represents a row returned from a query.
 * @author Fred Montariol
 */
abstract class AbstractRow(private val columnPropertyIndexMap: Map<KProperty1<*, *>, Int>) : ValueProvider {

	override operator fun get(stringProperty: KProperty1<*, String>): String =
			this[columnPropertyIndexMap[stringProperty]!!]!!

	override operator fun get(nullableStringProperty: KProperty1<*, String?>, `_`: Nullable): String? =
			this[columnPropertyIndexMap[nullableStringProperty]!!]

	override operator fun get(localDateTimeProperty: KProperty1<*, LocalDateTime>): LocalDateTime =
			this[columnPropertyIndexMap[localDateTimeProperty]!!]!!

	override operator fun get(nullableLocalDateTimeProperty: KProperty1<*, LocalDateTime?>, `_`: Nullable): LocalDateTime? =
			this[columnPropertyIndexMap[nullableLocalDateTimeProperty]!!]

	override operator fun get(dateProperty: KProperty1<*, Date>): Date =
			this[columnPropertyIndexMap[dateProperty]!!]!!

	override operator fun get(nullableDateProperty: KProperty1<*, Date?>, `_`: Nullable): Date? =
			this[columnPropertyIndexMap[nullableDateProperty]!!]

	/**
	 * Returns the element at the specified index in the list of returned fields of row
	 */
	protected abstract fun <T> get(index: Int, type: Class<T>): T?

	private inline operator fun <reified T> get(index: Int) = get(index, T::class.java)
}
