/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc


interface Repository {
    fun init()

    fun delete()
}
