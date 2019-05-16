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
open class ColumnPropertyProvider {
    protected fun <T : Any> colProp(stringProperty: KProperty1<T, String>, alias: String? = null) =
            NotNullStringColumnProperty(stringProperty, alias)

    protected fun <T : Any> colProp(localDateTimeProperty: KProperty1<T, LocalDateTime>, alias: String? = null) =
            NotNullLocalDateTimeColumnProperty(localDateTimeProperty, alias)

    protected fun <T : Any> colProp(dateProperty: KProperty1<T, Date>, alias: String? = null) =
            NotNullDateColumnProperty(dateProperty, alias)

    protected fun <T : Any> colProp(
            nullableStringProperty: KProperty1<T, String?>,
            alias: String? = null
    ): NullableStringColumnProperty<T> {
        checkNullableProperty(nullableStringProperty)
        return NullableStringColumnProperty(nullableStringProperty, alias)
    }

    protected fun <T : Any> colProp(
            nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>,
            alias: String? = null
    ): NullableLocalDateTimeColumnProperty<T> {
        checkNullableProperty(nullableLocalDateTimeProperty)
        return NullableLocalDateTimeColumnProperty(nullableLocalDateTimeProperty, alias)
    }

    protected fun <T : Any> colProp(
            nullableDateProperty: KProperty1<T, Date?>,
            alias: String? = null
    ): NullableDateColumnProperty<T> {
        checkNullableProperty(nullableDateProperty)
        return NullableDateColumnProperty(nullableDateProperty, alias)
    }

    private fun checkNullableProperty(property: KProperty1<*, *>) {
        require(property.returnType.isMarkedNullable) {
            "\"${property.name}\" is not a nullable property"
        }
    }
}
