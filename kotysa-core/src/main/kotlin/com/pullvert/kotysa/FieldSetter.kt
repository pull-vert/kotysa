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
interface FieldSetter<T : Any> {

    operator fun set(getter: (T) -> String, value: String)

    operator fun set(getter: (T) -> String?, value: String?): Nullable

    operator fun set(getter: (T) -> LocalDateTime, value: LocalDateTime)

    operator fun set(getter: (T) -> LocalDateTime?, value: LocalDateTime?): Nullable

    operator fun set(getter: (T) -> LocalDate, value: LocalDate)

    operator fun set(getter: (T) -> LocalDate?, value: LocalDate?): Nullable

    operator fun set(getter: (T) -> OffsetDateTime, value: OffsetDateTime)

    operator fun set(getter: (T) -> OffsetDateTime?, value: OffsetDateTime?): Nullable

    operator fun set(getter: (T) -> LocalTime, value: LocalTime)

    operator fun set(getter: (T) -> LocalTime?, value: LocalTime?): Nullable

    operator fun set(getter: (T) -> Boolean, value: Boolean)

    operator fun set(getter: (T) -> UUID, value: UUID)

    operator fun set(getter: (T) -> UUID?, value: UUID?): Nullable

    operator fun set(getter: (T) -> Int, value: Int)

    operator fun set(getter: (T) -> Int?, value: Int?): Nullable
}
