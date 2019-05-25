/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class FieldProvider {

    internal abstract val availableColumns: Map<KProperty1<*, *>, Column<*, *>>

    internal fun <T : Any> getField(property: KProperty1<T, String>, alias: String?) =
            NotNullStringColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, String?>, alias: String?) =
            NullableStringColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDateTime>, alias: String?) =
            NotNullLocalDateTimeColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDateTime?>, alias: String?) =
            NullableLocalDateTimeColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDate>, alias: String?) =
            NotNullLocalDateColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDate?>, alias: String?) =
            NullableLocalDateColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Instant>, alias: String?) =
            NotNullInstantColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Instant?>, alias: String?) =
            NullableInstantColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalTime>, alias: String?) =
            NotNullLocalTimeColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalTime?>, alias: String?) =
            NullableLocalTimeColumnField(availableColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Boolean>, alias: String?) =
            NotNullBooleanColumnField(availableColumns, property, alias)
}
