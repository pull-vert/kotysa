/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class FieldProvider {

    internal abstract val allColumns: Map<KProperty1<*, *>, Column<*, *>>

    internal fun getField(stringProperty: KProperty1<*, String?>): StringColumnField<*, String?> {
        requireAllColumnsContainsProperty(stringProperty)
        return StringColumnField(allColumns[stringProperty]!! as Column<*, String?>)
    }

    internal fun getField(localDateTimeProperty: KProperty1<*, LocalDateTime?>): LocalDateTimeColumnField<*, LocalDateTime?> {
        requireAllColumnsContainsProperty(localDateTimeProperty)
        return LocalDateTimeColumnField(allColumns[localDateTimeProperty]!! as Column<*, LocalDateTime?>)
    }

    internal fun getField(localDateTimeProperty: KProperty1<*, Date?>): DateColumnField<*, Date?> {
        requireAllColumnsContainsProperty(localDateTimeProperty)
        return DateColumnField(allColumns[localDateTimeProperty]!! as Column<*, Date?>)
    }

    private fun requireAllColumnsContainsProperty(columnProperty: KProperty1<*, *>) {
        require(allColumns.containsKey(columnProperty)) { "Requested field \"${columnProperty.name}\" is not mapped" }
    }
}

/**
 * @author Fred Montariol
 */
interface Field {
    val fieldName: String
}

/**
 * @author Fred Montariol
 */
internal abstract class ColumnField<T : Any, U>(column: Column<T, U>) : Column<T, U> by column, Field {
    override val fieldName: String
        get() = table.name + "." + columnName
}

/**
 * @author Fred Montariol
 */
internal class StringColumnField<T : Any, U>(column: Column<T, U>) : ColumnField<T, U>(column)

/**
 * @author Fred Montariol
 */
internal class LocalDateTimeColumnField<T : Any, U>(column: Column<T, U>) : ColumnField<T, U>(column)

/**
 * @author Fred Montariol
 */
internal class DateColumnField<T : Any, U>(column: Column<T, U>) : ColumnField<T, U>(column)
