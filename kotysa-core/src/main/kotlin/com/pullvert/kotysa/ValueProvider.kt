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
interface ValueProvider {

    operator fun <T : Any> get(getter: (T) -> String, alias: String? = null): String

    operator fun <T : Any> get(getter: (T) -> String?, alias: String? = null, `_`: Nullable = Nullable.TRUE): String?

    operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String? = null): LocalDateTime

    operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDateTime?

    operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String? = null): LocalDate

    operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalDate?

    operator fun <T : Any> get(getter: (T) -> Instant, alias: String? = null): Instant

    operator fun <T : Any> get(getter: (T) -> Instant?, alias: String? = null, `_`: Nullable = Nullable.TRUE): Instant?

    operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String? = null): LocalTime

    operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String? = null, `_`: Nullable = Nullable.TRUE): LocalTime?

    operator fun <T : Any> get(getter: (T) -> Boolean, alias: String? = null): Boolean
}

/**
 * Represents a row returned from a query.
 * @author Fred Montariol
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractRow(private val columnPropertyIndexMap: Map<out (Any) -> Any?, Int>) : ValueProvider {

    override operator fun <T : Any> get(getter: (T) -> String, alias: String?): String =
            this[columnPropertyIndexMap[getter as (Any) -> String]!!]!!

    override operator fun <T : Any> get(getter: (T) -> String?, alias: String?, `_`: Nullable): String? =
            this[columnPropertyIndexMap[getter as (Any) -> String?]!!]

    override operator fun <T : Any> get(getter: (T) -> LocalDateTime, alias: String?): LocalDateTime =
            this[columnPropertyIndexMap[getter as (Any) -> LocalDateTime]!!]!!

    override operator fun <T : Any> get(getter: (T) -> LocalDateTime?, alias: String?, `_`: Nullable): LocalDateTime? =
            this[columnPropertyIndexMap[getter as (Any) -> LocalDateTime?]!!]

    override operator fun <T : Any> get(getter: (T) -> LocalDate, alias: String?): LocalDate =
            this[columnPropertyIndexMap[getter as (Any) -> LocalDate]!!]!!

    override operator fun <T : Any> get(getter: (T) -> LocalDate?, alias: String?, `_`: Nullable): LocalDate? =
            this[columnPropertyIndexMap[getter as (Any) -> LocalDate?]!!]

    override operator fun <T : Any> get(getter: (T) -> Instant, alias: String?): Instant =
            this[columnPropertyIndexMap[getter as (Any) -> Instant]!!]!!

    override operator fun <T : Any> get(getter: (T) -> Instant?, alias: String?, `_`: Nullable): Instant? =
            this[columnPropertyIndexMap[getter as (Any) -> Instant?]!!]

    override operator fun <T : Any> get(getter: (T) -> LocalTime, alias: String?): LocalTime =
            this[columnPropertyIndexMap[getter as (Any) -> LocalTime]!!]!!

    override operator fun <T : Any> get(getter: (T) -> LocalTime?, alias: String?, `_`: Nullable): LocalTime? =
            this[columnPropertyIndexMap[getter as (Any) -> LocalTime?]!!]

    override operator fun <T : Any> get(getter: (T) -> Boolean, alias: String?): Boolean =
            this[columnPropertyIndexMap[getter as (Any) -> Boolean]!!]!!

    /**
     * Returns the element at the specified index in the list of returned fields of row
     */
    protected abstract fun <T> get(index: Int, type: Class<T>): T?

    private inline operator fun <reified T> get(index: Int) = get(index, T::class.java)
}
