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
@KotysaMarker
class WhereDsl<T : Any> internal constructor(
        private val init: WhereDsl<T>.(WhereFieldProvider) -> WhereClause,
        override val allColumns: Map<KProperty1<*, *>, Column<*, *>>
) : FieldProvider(), WhereFieldProvider {

    infix fun <U : Any> NotNullStringColumnField<U>.eq(stringValue: String) = WhereClause(this, Operation.EQ, stringValue)

    infix fun <U : Any> NullableStringColumnField<U>.eq(stringValue: String?) = WhereClause(this, Operation.EQ, stringValue)


    override fun <T : Any> get(property: KProperty1<T, String>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, String?>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalDateTime>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalDateTime?>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalDate>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalDate?>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, Instant>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, Instant?>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalTime>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, LocalTime?>, alias: String?) = getField(property, alias)

    override fun <T : Any> get(property: KProperty1<T, Boolean>, alias: String?) = getField(property, alias)

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): WhereClause {
        return init(this)
    }
}
