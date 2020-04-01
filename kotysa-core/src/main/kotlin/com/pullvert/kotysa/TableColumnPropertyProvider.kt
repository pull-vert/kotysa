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
public interface TableColumnPropertyProvider<T : Any> {

    public operator fun get(getter: (T) -> String): NotNullStringColumnProperty<T>

    public operator fun get(getter: (T) -> String?): NullableStringColumnProperty<T>

    public operator fun get(getter: (T) -> LocalDateTime): NotNullLocalDateTimeColumnProperty<T>

    public operator fun get(getter: (T) -> LocalDateTime?): NullableLocalDateTimeColumnProperty<T>

    public operator fun get(getter: (T) -> LocalDate): NotNullLocalDateColumnProperty<T>

    public operator fun get(getter: (T) -> LocalDate?): NullableLocalDateColumnProperty<T>

    public operator fun get(getter: (T) -> OffsetDateTime): NotNullOffsetDateTimeColumnProperty<T>

    public operator fun get(getter: (T) -> OffsetDateTime?): NullableOffsetDateTimeColumnProperty<T>

    public operator fun get(getter: (T) -> LocalTime): NotNullLocalTimeColumnProperty<T>

    public operator fun get(getter: (T) -> LocalTime?): NullableLocalTimeColumnProperty<T>

    public operator fun get(getter: (T) -> Boolean): NotNullBooleanColumnProperty<T>

    public operator fun get(getter: (T) -> UUID): NotNullUuidColumnProperty<T>

    public operator fun get(getter: (T) -> UUID?): NullableUuidColumnProperty<T>

    public operator fun get(getter: (T) -> Int): NotNullIntColumnProperty<T>

    public operator fun get(getter: (T) -> Int?): NullableIntColumnProperty<T>
}
