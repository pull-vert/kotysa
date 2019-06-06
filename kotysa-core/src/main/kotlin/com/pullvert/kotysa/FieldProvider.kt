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
interface FieldProvider {
    operator fun <T : Any> get(getter: (T) -> String, alias: String? = null): NotNullStringColumnField<T>

    operator fun <T : Any> get(getter: (T) -> String?, alias: String? = null): NullableStringColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String? = null): NotNullLocalDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String? = null): NullableLocalDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String? = null): NotNullLocalDateColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String? = null): NullableLocalDateColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Instant, alias: String? = null): NotNullInstantColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Instant?, alias: String? = null): NullableInstantColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String? = null): NotNullLocalTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String? = null): NullableLocalTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Boolean, alias: String? = null): NotNullBooleanColumnField<T>
}

/**
 * @author Fred Montariol
 */
interface TypedFieldProvider<T : Any> {
    operator fun get(getter: (T) -> String, alias: String? = null): NotNullStringColumnField<T>

    operator fun get(getter: (T) -> String?, alias: String? = null): NullableStringColumnField<T>

    operator fun get(getter: (T) -> LocalDateTime, alias: String? = null): NotNullLocalDateTimeColumnField<T>

    operator fun get(getter: (T) -> LocalDateTime?, alias: String? = null): NullableLocalDateTimeColumnField<T>

    operator fun get(getter: (T) -> LocalDate, alias: String? = null): NotNullLocalDateColumnField<T>

    operator fun get(getter: (T) -> LocalDate?, alias: String? = null): NullableLocalDateColumnField<T>

    operator fun get(getter: (T) -> Instant, alias: String? = null): NotNullInstantColumnField<T>

    operator fun get(getter: (T) -> Instant?, alias: String? = null): NullableInstantColumnField<T>

    operator fun get(getter: (T) -> LocalTime, alias: String? = null): NotNullLocalTimeColumnField<T>

    operator fun get(getter: (T) -> LocalTime?, alias: String? = null): NullableLocalTimeColumnField<T>

    operator fun get(getter: (T) -> Boolean, alias: String? = null): NotNullBooleanColumnField<T>
}

open class SimpleFieldProvider(
        override val availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldAccess(), FieldProvider {
    override fun <T : Any> get(getter: (T) -> String, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> String?, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDate, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDate?, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Instant, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Instant?, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalTime, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalTime?, alias: String?) = getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Boolean, alias: String?) = getField(getter, alias)
}

open class SimpleTypedFieldProvider<T : Any>(
        override val availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldAccess(), TypedFieldProvider<T> {
    override fun get(getter: (T) -> String, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> String?, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalDateTime, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalDateTime?, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalDate, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalDate?, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> Instant, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> Instant?, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalTime, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> LocalTime?, alias: String?) = getField(getter, alias)

    override fun get(getter: (T) -> Boolean, alias: String?) = getField(getter, alias)
}
