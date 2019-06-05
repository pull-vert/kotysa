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
class WhereDsl internal constructor(
        private val init: WhereDsl.(FieldProvider) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : SimpleFieldProvider(availableColumns) {

    infix fun <U : Any> NotNullStringColumnField<U>.eq(stringValue: String) = WhereClause(this, Operation.EQ, stringValue)

    infix fun <U : Any> NullableStringColumnField<U>.eq(stringValue: String?) = WhereClause(this, Operation.EQ, stringValue)

    @Suppress("UNCHECKED_CAST")
    internal fun initialize(): WhereClause {
        return init(this)
    }
}
