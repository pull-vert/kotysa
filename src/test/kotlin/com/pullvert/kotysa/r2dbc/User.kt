/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

val jdoe = User("jdoe", "John", "Doe")
val bboss = User("bboss", "Big", "Boss", "TheBoss")

/**
 * @author Fred Montariol
 */
data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val alias: String? = null
) {
//	val id: Int? = null // generated auto-increment
}

/**
 * @author Fred Montariol
 */
data class UserDto(
        val name: String,
        val alias: String?
)
