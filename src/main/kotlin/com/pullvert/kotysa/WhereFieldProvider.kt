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
interface WhereFieldProvider {
    operator fun <T : Any> get(property: KProperty1<T, String>, alias: String? = null): NotNullStringColumnField<T>

    operator fun <T : Any> get(property: KProperty1<T, String?>, alias: String? = null): NullableStringColumnField<T>

    operator fun <T : Any> get(property: KProperty1<T, LocalDateTime>, alias: String? = null): NotNullLocalDateTimeColumnField<T>

    operator fun <T : Any> get(property: KProperty1<T, LocalDateTime?>, alias: String? = null): NullableLocalDateTimeColumnField<T>

    operator fun <T : Any> get(property: KProperty1<T, LocalDate>, alias: String? = null): NotNullLocalDateColumnField<T>

    operator fun <T : Any> get(property: KProperty1<T, LocalDate?>, alias: String? = null): NullableLocalDateColumnField<T>
}
