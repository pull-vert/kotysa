/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * A table with an alias (that may be null)
 * @author Fred Montariol
 */
internal class AliasedTable<T : Any> internal constructor(
        internal val table: Table<T>,
        internal val alias: String? = null
) : Table<T> by table {

    /**
     * The prefix : alias if exists, or table name
     */
    internal val prefix = alias ?: name

    /**
     * Declaration in queries : "tableName AS alias" if alias exists, or "tableName"
     */
    internal val declaration = if (alias != null) {
        "$name AS $alias"
    } else {
        name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AliasedTable<*>

        if (alias != other.alias) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (alias?.hashCode() ?: 0)
        return result
    }
}
