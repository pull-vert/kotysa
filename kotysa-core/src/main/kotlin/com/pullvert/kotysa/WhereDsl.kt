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
@KotysaMarker
interface CommonWhereDsl {

    // operations on String

    infix fun <T : Any> NotNullStringColumnField<T>.eq(value: String) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullStringColumnField<T>.notEq(value: String) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullStringColumnField<T>.contains(value: String) =
            WhereClause(this, Operation.CONTAINS, "%$value%")

    infix fun <T : Any> NotNullStringColumnField<T>.startsWith(value: String) =
            WhereClause(this, Operation.STARTS_WITH, "$value%")

    infix fun <T : Any> NotNullStringColumnField<T>.endsWith(value: String) =
            WhereClause(this, Operation.ENDS_WITH, "%$value")

    infix fun <T : Any> NullableStringColumnField<T>.eq(value: String?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableStringColumnField<T>.notEq(value: String?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableStringColumnField<T>.contains(value: String) =
            WhereClause(this, Operation.CONTAINS, "%$value%")

    infix fun <T : Any> NullableStringColumnField<T>.startsWith(value: String) =
            WhereClause(this, Operation.STARTS_WITH, "$value%")

    infix fun <T : Any> NullableStringColumnField<T>.endsWith(value: String) =
            WhereClause(this, Operation.ENDS_WITH, "%$value")

    // operations on java.util.UUID

    infix fun <T : Any> NotNullUuidColumnField<T>.eq(value: UUID) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullUuidColumnField<T>.notEq(value: UUID) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableUuidColumnField<T>.eq(value: UUID?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableUuidColumnField<T>.notEq(value: UUID?) =
            WhereClause(this, Operation.NOT_EQ, value)

    // operations on java.time.LocalDate

    infix fun <T : Any> NotNullLocalDateColumnField<T>.eq(value: LocalDate) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullLocalDateColumnField<T>.notEq(value: LocalDate) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullLocalDateColumnField<T>.before(value: LocalDate) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NotNullLocalDateColumnField<T>.after(value: LocalDate) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NotNullLocalDateColumnField<T>.beforeOrEq(value: LocalDate) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NotNullLocalDateColumnField<T>.afterOrEq(value: LocalDate) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.eq(value: LocalDate?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.notEq(value: LocalDate?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.before(value: LocalDate) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.after(value: LocalDate) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.beforeOrEq(value: LocalDate) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NullableLocalDateColumnField<T>.afterOrEq(value: LocalDate) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    // operations on java.time.LocalDateTime

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.eq(value: LocalDateTime) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.notEq(value: LocalDateTime) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.before(value: LocalDateTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.after(value: LocalDateTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.beforeOrEq(value: LocalDateTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NotNullLocalDateTimeColumnField<T>.afterOrEq(value: LocalDateTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.eq(value: LocalDateTime?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.notEq(value: LocalDateTime?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.before(value: LocalDateTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.after(value: LocalDateTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.beforeOrEq(value: LocalDateTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NullableLocalDateTimeColumnField<T>.afterOrEq(value: LocalDateTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    // operations on java.time.OffsetDateTime

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.eq(value: OffsetDateTime) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.notEq(value: OffsetDateTime) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.before(value: OffsetDateTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.after(value: OffsetDateTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.beforeOrEq(value: OffsetDateTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NotNullOffsetDateTimeColumnField<T>.afterOrEq(value: OffsetDateTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.eq(value: OffsetDateTime?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.notEq(value: OffsetDateTime?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.before(value: OffsetDateTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.after(value: OffsetDateTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.beforeOrEq(value: OffsetDateTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NullableOffsetDateTimeColumnField<T>.afterOrEq(value: OffsetDateTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    // operations on java.time.LocalTime

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.eq(value: LocalTime) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.notEq(value: LocalTime) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.before(value: LocalTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.after(value: LocalTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.beforeOrEq(value: LocalTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NotNullLocalTimeColumnField<T>.afterOrEq(value: LocalTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.eq(value: LocalTime?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.notEq(value: LocalTime?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.before(value: LocalTime) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.after(value: LocalTime) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.beforeOrEq(value: LocalTime) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NullableLocalTimeColumnField<T>.afterOrEq(value: LocalTime) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    // operations on Boolean

    infix fun <T : Any> NotNullBooleanColumnField<T>.eq(value: Boolean) =
            // SqLite does not support Boolean literal
            if (dbType == DbType.SQLITE) {
                val intValue = if (value) 1 else 0
                WhereClause(this, Operation.EQ, intValue)
            } else {
                WhereClause(this, Operation.EQ, value)
            }

    // operations on Int

    infix fun <T : Any> NotNullIntColumnField<T>.eq(value: Int) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NotNullIntColumnField<T>.notEq(value: Int) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NotNullIntColumnField<T>.inf(value: Int) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NotNullIntColumnField<T>.sup(value: Int) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NotNullIntColumnField<T>.infOrEq(value: Int) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NotNullIntColumnField<T>.supOrEq(value: Int) =
            WhereClause(this, Operation.SUP_OR_EQ, value)

    infix fun <T : Any> NullableIntColumnField<T>.eq(value: Int?) =
            WhereClause(this, Operation.EQ, value)

    infix fun <T : Any> NullableIntColumnField<T>.notEq(value: Int?) =
            WhereClause(this, Operation.NOT_EQ, value)

    infix fun <T : Any> NullableIntColumnField<T>.inf(value: Int) =
            WhereClause(this, Operation.INF, value)

    infix fun <T : Any> NullableIntColumnField<T>.sup(value: Int) =
            WhereClause(this, Operation.SUP, value)

    infix fun <T : Any> NullableIntColumnField<T>.infOrEq(value: Int) =
            WhereClause(this, Operation.INF_OR_EQ, value)

    infix fun <T : Any> NullableIntColumnField<T>.supOrEq(value: Int) =
            WhereClause(this, Operation.SUP_OR_EQ, value)
}

/**
 * @author Fred Montariol
 */
class WhereDsl internal constructor(
        private val init: WhereDsl.(FieldProvider) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        dbType: DbType
) : SimpleFieldProvider(availableColumns, dbType), CommonWhereDsl {

    internal fun initialize(): WhereClause {
        return init(this)
    }
}

/**
 * @author Fred Montariol
 */
class TypedWhereDsl<T : Any> internal constructor(
        private val init: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        dbType: DbType
) : SimpleTypedFieldProvider<T>(availableColumns, dbType), CommonWhereDsl {

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): WhereClause {
        return init(this)
    }
}
