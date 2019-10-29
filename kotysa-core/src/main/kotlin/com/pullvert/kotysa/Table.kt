/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * A database Table model mapped by entity class [tableClass]
 *
 * @author Fred Montariol
 */
interface Table<T : Any> {
    val tableClass: KClass<T>
    /**
     * Real name of this table in the database
     */
    val name: String
    val columns: Map<(T) -> Any?, Column<T, *>>
    val primaryKey: PrimaryKey
    val foreignKeys: Set<ForeignKey>
}

/**
 * @author Fred Montariol
 */
internal class TableImpl<T : Any> internal constructor(
        override val tableClass: KClass<T>,
        override val name: String,
        override val columns: Map<(T) -> Any?, Column<T, *>>,
        override val primaryKey: PrimaryKey,
        override val foreignKeys: Set<ForeignKey>
) : Table<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableImpl<*>

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

/**
 * All Mapped Tables
 * @author Fred Montariol
 */
class Tables internal constructor(
        val allTables: Map<KClass<*>, Table<*>>,
        internal val allColumns: Map<out (Any) -> Any?, Column<*, *>>,
        internal val dbType: DbType
)
