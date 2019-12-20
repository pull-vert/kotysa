/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sample

import com.pullvert.kotysa.tables
import java.util.*

fun postgresqlTables() =
        tables().postgresql { // choose database type
            table<PostgresUser> {
                name = "users"
                column { it[PostgresUser::id].uuid().primaryKey() }
                column { it[PostgresUser::firstname].varchar().name("fname") }
                column { it[PostgresUser::lastname].varchar().name("lname") }
                column { it[PostgresUser::isAdmin].boolean() }
                column { it[PostgresUser::alias].varchar() }
            }
        }

data class PostgresUser(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: UUID
)
