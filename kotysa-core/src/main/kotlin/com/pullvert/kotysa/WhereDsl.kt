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
@KotysaMarker
class WhereDsl<T : Any> internal constructor(
        private val init: WhereDsl<T>.(WhereFieldProvider) -> WhereClause,
        override val availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : FieldProvider(), WhereFieldProvider {

    infix fun <U : Any> NotNullStringColumnField<U>.eq(stringValue: String) = WhereClause(this, Operation.EQ, stringValue)

    infix fun <U : Any> NullableStringColumnField<U>.eq(stringValue: String?) = WhereClause(this, Operation.EQ, stringValue)


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

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): WhereClause {
        return init(this)
    }
}
