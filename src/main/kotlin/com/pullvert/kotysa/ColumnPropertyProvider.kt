/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
open class ColumnPropertyProvider {
    protected fun <T : Any> colProp(property: KProperty1<T, String>) = NotNullStringColumnProperty(property)

    protected fun <T : Any> colProp(property: KProperty1<T, LocalDateTime>) = NotNullLocalDateTimeColumnProperty(property)

    protected fun <T : Any> colProp(property: KProperty1<T, LocalDate>) = NotNullLocalDateColumnProperty(property)

    protected fun <T : Any> colProp(property: KProperty1<T, String?>): NullableStringColumnProperty<T> {
        checkNullableProperty(property)
        return NullableStringColumnProperty(property)
    }

    protected fun <T : Any> colProp(property: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateTimeColumnProperty(property)
    }

    protected fun <T : Any> colProp(property: KProperty1<T, LocalDate?>): NullableLocalDateColumnProperty<T> {
        checkNullableProperty(property)
        return NullableLocalDateColumnProperty(property)
    }

    private fun checkNullableProperty(property: KProperty1<*, *>) {
        require(property.returnType.isMarkedNullable) { "\"${property.name}\" is not a nullable property" }
    }
}
