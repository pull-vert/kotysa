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
interface TableColumnPropertyProvider<T : Any> {
    operator fun get(stringProperty: KProperty1<T, String>): NotNullStringColumnProperty<T>

    operator fun get(localDateTimeProperty: KProperty1<T, LocalDateTime>): NotNullLocalDateTimeColumnProperty<T>

    operator fun get(dateProperty: KProperty1<T, LocalDate>): NotNullLocalDateColumnProperty<T>

    operator fun get(nullableStringProperty: KProperty1<T, String?>): NullableStringColumnProperty<T>

    operator fun get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T>

    operator fun get(nullableDateProperty: KProperty1<T, LocalDate?>): NullableLocalDateColumnProperty<T>
}
