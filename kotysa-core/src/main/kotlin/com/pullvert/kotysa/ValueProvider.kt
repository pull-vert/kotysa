/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
public interface ValueProvider {

    public operator fun <T : Any> get(getter: (T) -> String, alias: String? = null): String

    public operator fun <T : Any> get(getter: (T) -> String?, alias: String? = null, `_`: Nullable = Nullable.TRUE): String?

    public operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String? = null): LocalDateTime

    public operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDateTime?

    public operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String? = null): LocalDate

    public operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDate?

    public operator fun <T : Any> get(getter: (T) -> OffsetDateTime, alias: String? = null): OffsetDateTime

    public operator fun <T : Any> get(getter: (T) -> OffsetDateTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): OffsetDateTime?

    public operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String? = null): LocalTime

    public operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalTime?

    public operator fun <T : Any> get(getter: (T) -> Boolean, alias: String? = null): Boolean

    public operator fun <T : Any> get(getter: (T) -> UUID, alias: String? = null): UUID

    public operator fun <T : Any> get(getter: (T) -> UUID?, alias: String? = null, `_`: Nullable = Nullable.TRUE): UUID?

    public operator fun <T : Any> get(getter: (T) -> Int, alias: String? = null): Int

    public operator fun <T : Any> get(getter: (T) -> Int?, alias: String? = null, `_`: Nullable = Nullable.TRUE): Int?
}
