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
interface Field {
    val fieldName: String
    val alias: String?
}

/**
 * @author Fred Montariol
 */
interface NotNullField : Field

/**
 * @author Fred Montariol
 */
interface NullableField : Field

class CountField<T : Any, U> internal constructor(
        internal val dsl: ((FieldProvider) -> ColumnField<T, *>)?,
        private val columnField: ColumnField<T, U>?,
        override val alias: String?
) : NotNullField {
    override val fieldName: String
        get() {
            val counted = columnField?.fieldName ?: "*"
            return "COUNT($counted)"
        }

}

/**
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class ColumnField<T : Any, U> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Any?,
        final override val alias: String?
) : Field {

    internal val column: Column<T, U>

    init {
        if (alias != null) {
            require(alias.isNotBlank()) { "An alias must not be empty or blank" }
        }
        require(availableColumns.containsKey(getter)) { "Requested field \"$getter\" is not mapped" }
        column = availableColumns[getter]!! as Column<T, U>
    }

    override val fieldName =
            if (alias != null) {
                alias + "." + column.name
            } else {
                column.table.name + "." + column.name
            }
}

/**
 * @author Fred Montariol
 */
class NotNullStringColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> String,
        alias: String? = null
) : ColumnField<T, String>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableStringColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> String?,
        alias: String? = null
) : ColumnField<T, String?>(availableColumns, getter, alias), NullableField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDateTime,
        alias: String? = null
) : ColumnField<T, LocalDateTime>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDateTime?,
        alias: String? = null
) : ColumnField<T, LocalDateTime?>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDate,
        alias: String? = null
) : ColumnField<T, LocalDate>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDate?,
        alias: String? = null
) : ColumnField<T, LocalDate?>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullInstantColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Instant,
        alias: String? = null
) : ColumnField<T, Instant>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableInstantColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Instant?,
        alias: String? = null
) : ColumnField<T, Instant?>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalTime,
        alias: String? = null
) : ColumnField<T, LocalTime>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalTime?,
        alias: String? = null
) : ColumnField<T, LocalTime?>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullBooleanColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Boolean,
        alias: String? = null
) : ColumnField<T, Boolean>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullUuidColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> UUID,
        alias: String? = null
) : ColumnField<T, UUID>(availableColumns, getter, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableUuidColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> UUID?,
        alias: String? = null
) : ColumnField<T, UUID?>(availableColumns, getter, alias), NotNullField
