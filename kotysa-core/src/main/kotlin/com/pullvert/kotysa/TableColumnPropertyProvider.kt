/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Fred Montariol
 */
interface TableColumnPropertyProvider<T : Any> {

    operator fun get(getter: (T) -> String): NotNullStringColumnProperty<T>

    operator fun get(getter: (T) -> String?): NullableStringColumnProperty<T>

    operator fun get(getter: (T) -> LocalDateTime): NotNullLocalDateTimeColumnProperty<T>

    operator fun get(getter: (T) -> LocalDateTime?): NullableLocalDateTimeColumnProperty<T>

    operator fun get(getter: (T) -> LocalDate): NotNullLocalDateColumnProperty<T>

    operator fun get(getter: (T) -> LocalDate?): NullableLocalDateColumnProperty<T>

    operator fun get(getter: (T) -> Instant): NotNullInstantColumnProperty<T>

    operator fun get(getter: (T) -> Instant?): NullableInstantColumnProperty<T>

    operator fun get(getter: (T) -> LocalTime): NotNullLocalTimeColumnProperty<T>

    operator fun get(getter: (T) -> LocalTime?): NullableLocalTimeColumnProperty<T>

    operator fun get(getter: (T) -> Boolean): NotNullBooleanColumnProperty<T>
}
