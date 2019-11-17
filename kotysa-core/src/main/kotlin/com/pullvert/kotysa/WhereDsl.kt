/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDate
import java.time.LocalDateTime
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
}

/**
 * @author Fred Montariol
 */
class WhereDsl internal constructor(
        private val init: WhereDsl.(FieldProvider) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : SimpleFieldProvider(availableColumns), CommonWhereDsl {

    internal fun initialize(): WhereClause {
        return init(this)
    }
}

/**
 * @author Fred Montariol
 */
class TypedWhereDsl<T : Any> internal constructor(
        private val init: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : SimpleTypedFieldProvider<T>(availableColumns), CommonWhereDsl {

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): WhereClause {
        return init(this)
    }
}
