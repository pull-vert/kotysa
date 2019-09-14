/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test.common.sample

import com.pullvert.kotysa.tables
import java.util.*

val h2tables =
        tables().h2 { // choose database type
            table<H2Role> {
                name = "roles"
                column { it[H2Role::id].uuid().primaryKey }
                column { it[H2Role::label].varchar() }
            }
            table<H2User> {
                name = "users"
                column { it[H2User::id].uuid().primaryKey }
                column { it[H2User::firstname].varchar().name("fname") }
                column { it[H2User::lastname].varchar().name("lname") }
                column { it[H2User::isAdmin].boolean() }
                column { it[H2User::roleId].uuid() }
                column { it[H2User::alias].varchar() }
            }
        }

data class H2Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

val h2User = H2Role("user")
val h2Admin = H2Role("admin")

data class H2User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val roleId: UUID,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)

val h2Jdoe = H2User("John", "Doe", false, h2User.id)
val h2Bboss = H2User("Big", "Boss", true, h2Admin.id, "TheBoss")
