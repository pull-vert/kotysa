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
@Suppress("UNCHECKED_CAST")
internal class FieldAccess internal constructor(private val availableColumns: Map<out (Any) -> Any?, Column<*, *>>) {

    internal fun <T : Any> getField(getter: (T) -> String, alias: String?) =
            NotNullStringColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> String?, alias: String?) =
            NullableStringColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDateTime, alias: String?) =
            NotNullLocalDateTimeColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDateTime?, alias: String?) =
            NullableLocalDateTimeColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDate, alias: String?) =
            NotNullLocalDateColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDate?, alias: String?) =
            NullableLocalDateColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> Instant, alias: String?) =
            NotNullInstantColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> Instant?, alias: String?) =
            NullableInstantColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalTime, alias: String?) =
            NotNullLocalTimeColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalTime?, alias: String?) =
            NullableLocalTimeColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> Boolean, alias: String?) =
            NotNullBooleanColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> UUID, alias: String?) =
            NotNullUuidColumnField(availableColumns, getter, alias)

    internal fun <T : Any> getField(getter: (T) -> UUID?, alias: String?) =
            NullableUuidColumnField(availableColumns, getter, alias)
}
