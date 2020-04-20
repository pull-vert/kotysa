/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa


public interface PrimaryKey {
    public val name: String?
}


internal class SinglePrimaryKey<T : Any, U> internal constructor(
        override val name: String?,
        internal val column: Column<T, U>
): PrimaryKey
