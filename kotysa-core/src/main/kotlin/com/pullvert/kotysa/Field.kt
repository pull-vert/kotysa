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

/**
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class ColumnField<T : Any, U> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<*, *>,
        final override val alias: String?
) : Field {

    internal val column: Column<T, U>

    init {
        if (alias != null) {
            require(alias.isNotBlank()) { "An alias must not be empty or blank" }
        }
        require(availableColumns.containsKey(property)) { "Requested field \"$property\" is not mapped" }
        column = availableColumns[property]!! as Column<T, U>
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
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, String>,
        alias: String? = null
) : ColumnField<T, String>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableStringColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, String?>,
        alias: String? = null
) : ColumnField<T, String?>(availableColumns, property, alias), NullableField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalDateTime>,
        alias: String? = null
) : ColumnField<T, LocalDateTime>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalDateTime?>,
        alias: String? = null
) : ColumnField<T, LocalDateTime?>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalDate>,
        alias: String? = null
) : ColumnField<T, LocalDate>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalDateColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalDate?>,
        alias: String? = null
) : ColumnField<T, LocalDate?>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullInstantColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, Instant>,
        alias: String? = null
) : ColumnField<T, Instant>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableInstantColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, Instant?>,
        alias: String? = null
) : ColumnField<T, Instant?>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalTime>,
        alias: String? = null
) : ColumnField<T, LocalTime>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NullableLocalTimeColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, LocalTime?>,
        alias: String? = null
) : ColumnField<T, LocalTime?>(availableColumns, property, alias), NotNullField

/**
 * @author Fred Montariol
 */
class NotNullBooleanColumnField<T : Any> internal constructor(
        availableColumns: Map<KProperty1<*, *>, Column<*, *>>,
        property: KProperty1<T, Boolean>,
        alias: String? = null
) : ColumnField<T, Boolean>(availableColumns, property, alias), NotNullField
