/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test.common.sample

import com.pullvert.kotysa.tables

val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::id].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
        }

data class User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: String
)

val jdoe = User("John", "Doe", false, id = "123")
val bboss = User("Big", "Boss", true, "TheBoss", "456")

data class UserDto(
        val name: String,
        val alias: String?
)
