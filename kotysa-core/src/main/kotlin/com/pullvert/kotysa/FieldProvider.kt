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

    internal abstract val allColumns: Map<KProperty1<*, *>, Column<*, *>>

    internal fun <T : Any> getField(property: KProperty1<T, String>, alias: String?) =
            NotNullStringColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, String?>, alias: String?) =
            NullableStringColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDateTime>, alias: String?) =
            NotNullLocalDateTimeColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDateTime?>, alias: String?) =
            NullableLocalDateTimeColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDate>, alias: String?) =
            NotNullLocalDateColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalDate?>, alias: String?) =
            NullableLocalDateColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Instant>, alias: String?) =
            NotNullInstantColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Instant?>, alias: String?) =
            NullableInstantColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalTime>, alias: String?) =
            NotNullLocalTimeColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, LocalTime?>, alias: String?) =
            NullableLocalTimeColumnField(allColumns, property, alias)

    internal fun <T : Any> getField(property: KProperty1<T, Boolean>, alias: String?) =
            NotNullBooleanColumnField(allColumns, property, alias)
}
