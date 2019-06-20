/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.samples

import com.pullvert.kotysa.tables
import java.util.*

fun tables() =
        tables {
            table<User> {
                name = "users"
                column { it[User::id].uuid().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
        }

data class User(
        val id: UUID = UUID.randomUUID(),
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null
)

val jdoe = User(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), "John", "Doe", false)
val bboss = User(UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"), "Big", "Boss", true, "TheBoss")

data class UserDto(
        val name: String,
        val alias: String?
)
