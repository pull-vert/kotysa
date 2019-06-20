/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author Fred Montariol
 */
interface ValueProvider {

    operator fun <T : Any> get(getter: (T) -> String, alias: String? = null): String

    operator fun <T : Any> get(getter: (T) -> String?, alias: String? = null, `_`: Nullable = Nullable.TRUE): String?

    operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String? = null): LocalDateTime

    operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDateTime?

    operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String? = null): LocalDate

    operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDate?

    operator fun <T : Any> get(getter: (T) -> Instant, alias: String? = null): Instant

    operator fun <T : Any> get(getter: (T) -> Instant?, alias: String? = null, `_`: Nullable = Nullable.TRUE): Instant?

    operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String? = null): LocalTime

    operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalTime?

    operator fun <T : Any> get(getter: (T) -> Boolean, alias: String? = null): Boolean

    operator fun <T : Any> get(getter: (T) -> UUID, alias: String? = null): UUID

    operator fun <T : Any> get(getter: (T) -> UUID?, alias: String? = null, `_`: Nullable = Nullable.TRUE): UUID?
}
