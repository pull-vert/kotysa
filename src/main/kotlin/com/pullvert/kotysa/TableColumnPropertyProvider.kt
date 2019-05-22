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
interface TableColumnPropertyProvider<T : Any> {
    operator fun get(property: KProperty1<T, String>): NotNullStringColumnProperty<T>

    operator fun get(property: KProperty1<T, LocalDateTime>): NotNullLocalDateTimeColumnProperty<T>

    operator fun get(property: KProperty1<T, LocalDate>): NotNullLocalDateColumnProperty<T>

    operator fun get(property: KProperty1<T, Instant>): NotNullInstantColumnProperty<T>

    operator fun get(property: KProperty1<T, String?>): NullableStringColumnProperty<T>

    operator fun get(property: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T>

    operator fun get(property: KProperty1<T, LocalDate?>): NullableLocalDateColumnProperty<T>

    operator fun get(property: KProperty1<T, Instant?>): NullableInstantColumnProperty<T>
}
