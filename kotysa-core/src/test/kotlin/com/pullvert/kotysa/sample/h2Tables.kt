/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sample

import com.pullvert.kotysa.tables
import java.util.*

fun h2tables() =
        tables().h2 { // choose database type
            table<UserH2> {
                name = "users"
                column { it[UserH2::id].uuid().primaryKey }
                column { it[UserH2::firstname].varchar().name("fname") }
                column { it[UserH2::lastname].varchar().name("lname") }
                column { it[UserH2::isAdmin].boolean() }
                column { it[UserH2::alias].varchar() }
            }
        }

data class UserH2(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: UUID
)
