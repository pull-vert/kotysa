/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
@KotysaMarker
class WhereDsl<T : Any>(private val init: WhereDsl<T>.(WhereColumnPropertyProvider) -> WhereClause<*, *>) {

    infix fun <U : Any> NotNullStringColumnProperty<U>.EQ(stringValue: String): String =
            ""

    fun NullableStringColumnProperty<T>.varchar(): String =
            ""

    fun NotNullLocalDateTimeColumnProperty<T>.timestamp(): String =
            ""

    fun NullableLocalDateTimeColumnProperty<T>.timestamp(): String =
            ""

    fun NotNullDateColumnProperty<T>.timestamp(): String =
            ""

    fun NullableDateColumnProperty<T>.timestamp(): String =
            ""
//
//    @Suppress("UNCHECKED_CAST")
//    internal fun initialize(): Column<T, *> {
//        val columnBuilder = init(WhereColumnPropertyProviderImpl()) as AbstractColumn.ColumnBuilder<*, T>
//        if (!columnBuilder.columnNameInitialized) {
//            columnBuilder.columnName = columnBuilder.entityProperty.name
//        }
//        return columnBuilder.build()
//    }
}
