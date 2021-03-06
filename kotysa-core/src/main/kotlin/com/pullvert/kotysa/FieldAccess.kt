/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*


@Suppress("UNCHECKED_CAST")
internal class FieldAccess internal constructor(
        private val availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        private val dbType: DbType
) {

    internal fun <T : Any> getField(getter: (T) -> String, alias: String?) =
            NotNullStringColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> String?, alias: String?) =
            NullableStringColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDateTime, alias: String?) =
            NotNullLocalDateTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDateTime?, alias: String?) =
            NullableLocalDateTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDate, alias: String?) =
            NotNullLocalDateColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalDate?, alias: String?) =
            NullableLocalDateColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> OffsetDateTime, alias: String?) =
            NotNullOffsetDateTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> OffsetDateTime?, alias: String?) =
            NullableOffsetDateTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalTime, alias: String?) =
            NotNullLocalTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> LocalTime?, alias: String?) =
            NullableLocalTimeColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> Boolean, alias: String?) =
            NotNullBooleanColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> UUID, alias: String?) =
            NotNullUuidColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> UUID?, alias: String?) =
            NullableUuidColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> Int, alias: String?) =
            NotNullIntColumnField(availableColumns, getter, dbType, alias)

    internal fun <T : Any> getField(getter: (T) -> Int?, alias: String?) =
            NullableIntColumnField(availableColumns, getter, dbType, alias)
}
