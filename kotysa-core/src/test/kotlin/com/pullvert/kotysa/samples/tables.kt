/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.samples

import com.pullvert.kotysa.tables

val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
        }

data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null
)

val jdoe = User("jdoe", "John", "Doe", false)
val bboss = User("bboss", "Big", "Boss", true, "TheBoss")

data class UserDto(
        val name: String,
        val alias: String?
)
