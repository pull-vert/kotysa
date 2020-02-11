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
interface FieldProvider {
    operator fun <T : Any> get(getter: (T) -> String, alias: String? = null): NotNullStringColumnField<T>

    operator fun <T : Any> get(getter: (T) -> String?, alias: String? = null): NullableStringColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String? = null): NotNullLocalDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String? = null): NullableLocalDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String? = null): NotNullLocalDateColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String? = null): NullableLocalDateColumnField<T>

    operator fun <T : Any> get(getter: (T) -> OffsetDateTime, alias: String? = null): NotNullOffsetDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> OffsetDateTime?, alias: String? = null): NullableOffsetDateTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String? = null): NotNullLocalTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String? = null): NullableLocalTimeColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Boolean, alias: String? = null): NotNullBooleanColumnField<T>

    operator fun <T : Any> get(getter: (T) -> UUID, alias: String? = null): NotNullUuidColumnField<T>

    operator fun <T : Any> get(getter: (T) -> UUID?, alias: String? = null): NullableUuidColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Int, alias: String? = null): NotNullIntColumnField<T>

    operator fun <T : Any> get(getter: (T) -> Int?, alias: String? = null): NullableIntColumnField<T>
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

    operator fun get(getter: (T) -> OffsetDateTime, alias: String? = null): NotNullOffsetDateTimeColumnField<T>

    operator fun get(getter: (T) -> OffsetDateTime?, alias: String? = null): NullableOffsetDateTimeColumnField<T>

    operator fun get(getter: (T) -> LocalTime, alias: String? = null): NotNullLocalTimeColumnField<T>

    operator fun get(getter: (T) -> LocalTime?, alias: String? = null): NullableLocalTimeColumnField<T>

    operator fun get(getter: (T) -> Boolean, alias: String? = null): NotNullBooleanColumnField<T>

    operator fun get(getter: (T) -> UUID, alias: String? = null): NotNullUuidColumnField<T>

    operator fun get(getter: (T) -> UUID?, alias: String? = null): NullableUuidColumnField<T>

    operator fun get(getter: (T) -> Int, alias: String? = null): NotNullIntColumnField<T>

    operator fun get(getter: (T) -> Int?, alias: String? = null): NullableIntColumnField<T>
}

open class SimpleFieldProvider internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        dbType: DbType
) : FieldProvider {

    private val fieldAccess = FieldAccess(availableColumns, dbType)

    override fun <T : Any> get(getter: (T) -> String, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> String?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDate, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalDate?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> OffsetDateTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> OffsetDateTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> LocalTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Boolean, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> UUID, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> UUID?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Int, alias: String?) = fieldAccess.getField(getter, alias)

    override fun <T : Any> get(getter: (T) -> Int?, alias: String?) = fieldAccess.getField(getter, alias)
}

open class SimpleTypedFieldProvider<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        dbType: DbType
) : TypedFieldProvider<T> {

    private val fieldAccess = FieldAccess(availableColumns, dbType)

    override fun get(getter: (T) -> String, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> String?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalDateTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalDateTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalDate, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalDate?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> OffsetDateTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> OffsetDateTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalTime, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> LocalTime?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> Boolean, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> UUID, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> UUID?, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> Int, alias: String?) = fieldAccess.getField(getter, alias)

    override fun get(getter: (T) -> Int?, alias: String?) = fieldAccess.getField(getter, alias)
}
