/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
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
        columnField: ColumnField<T, U>?,
        override val alias: String?
) : NotNullField {
    override val fieldName: String

    init {
        val counted = columnField?.fieldName ?: "*"
        fieldName = "COUNT($counted)"
    }
}

/**
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class ColumnField<T : Any, U> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Any?,
        final override val alias: String?,
        internal val dbType: DbType
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
                "$alias."
            } else {
                "${column.table.name}."
            } + column.name
}

/**
 * @author Fred Montariol
 */
class NotNullStringColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> String,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, String>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableStringColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> String?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, String?>(availableColumns, getter, alias, dbType), NullableField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDateTime,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalDateTime>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDateTime?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalDateTime?>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDate,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalDate>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalDate?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalDate?>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullOffsetDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> OffsetDateTime,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, OffsetDateTime>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableOffsetDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> OffsetDateTime?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, OffsetDateTime?>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalTime,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalTime>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> LocalTime?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, LocalTime?>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullBooleanColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> Boolean,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, Boolean>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullUuidColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> UUID,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, UUID>(availableColumns, getter, alias, dbType), NotNullField

/**
 * @author Fred Montariol
 */
class NullableUuidColumnField<T : Any> internal constructor(
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>,
        getter: (T) -> UUID?,
        dbType: DbType,
        alias: String? = null
) : ColumnField<T, UUID?>(availableColumns, getter, alias, dbType), NotNullField
