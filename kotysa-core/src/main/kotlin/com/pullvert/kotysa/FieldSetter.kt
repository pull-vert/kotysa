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
public interface FieldSetter<T : Any> {

    public operator fun set(getter: (T) -> String, value: String)

    public operator fun set(getter: (T) -> String?, value: String?): Nullable

    public operator fun set(getter: (T) -> LocalDateTime, value: LocalDateTime)

    public operator fun set(getter: (T) -> LocalDateTime?, value: LocalDateTime?): Nullable

    public operator fun set(getter: (T) -> LocalDate, value: LocalDate)

    public operator fun set(getter: (T) -> LocalDate?, value: LocalDate?): Nullable

    public operator fun set(getter: (T) -> OffsetDateTime, value: OffsetDateTime)

    public operator fun set(getter: (T) -> OffsetDateTime?, value: OffsetDateTime?): Nullable

    public operator fun set(getter: (T) -> LocalTime, value: LocalTime)

    public operator fun set(getter: (T) -> LocalTime?, value: LocalTime?): Nullable

    public operator fun set(getter: (T) -> Boolean, value: Boolean)

    public operator fun set(getter: (T) -> UUID, value: UUID)

    public operator fun set(getter: (T) -> UUID?, value: UUID?): Nullable

    public operator fun set(getter: (T) -> Int, value: Int)

    public operator fun set(getter: (T) -> Int?, value: Int?): Nullable
}
