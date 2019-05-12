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
class ColumnChoiceProvider<T : Any> {
	operator fun get(stringProperty: KProperty1<T, String>) = NotNullStringColumnChoice(stringProperty)

	operator fun get(localDateTimeProperty: KProperty1<T, LocalDateTime>) = NotNullLocalDateTimeColumnChoice(localDateTimeProperty)

	operator fun get(dateProperty: KProperty1<T, Date>) = NotNullDateColumnChoice(dateProperty)

	operator fun get(nullableStringProperty: KProperty1<T, String?>): NullableStringColumnChoice<T> {
		checkNullableProperty(nullableStringProperty)
		return NullableStringColumnChoice(nullableStringProperty)
	}

	operator fun get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnChoice<T> {
		checkNullableProperty(nullableLocalDateTimeProperty)
		return NullableLocalDateTimeColumnChoice(nullableLocalDateTimeProperty)
	}

	operator fun get(nullableDateProperty: KProperty1<T, Date?>): NullableDateColumnChoice<T> {
		checkNullableProperty(nullableDateProperty)
		return NullableDateColumnChoice(nullableDateProperty)
	}

	private fun checkNullableProperty(property: KProperty1<*, *>) {
		require(property.returnType.isMarkedNullable) {
			"\"${property.name}\" is not a nullable property"
		}
	}
}
