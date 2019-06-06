/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
@KotysaMarker
interface CommonWhereDsl {
    infix fun <U : Any> NotNullStringColumnField<U>.eq(stringValue: String) = WhereClause(this, Operation.EQ, stringValue)

    infix fun <U : Any> NullableStringColumnField<U>.eq(stringValue: String?) = WhereClause(this, Operation.EQ, stringValue)
}

/**
 * @author Fred Montariol
 */
class WhereDsl internal constructor(
        private val init: WhereDsl.(FieldProvider) -> WhereClause,
        availableColumns: Map<out (Any) -> Any?, Column<*, *>>
) : SimpleFieldProvider(availableColumns), CommonWhereDsl {

    @Suppress("UNCHECKED_CAST")
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
