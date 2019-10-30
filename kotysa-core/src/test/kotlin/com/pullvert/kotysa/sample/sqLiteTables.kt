/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.sample

import com.pullvert.kotysa.tables

fun sqlitetables() =
        tables().sqlite { // choose database type
            table<SqLiteUser> {
                name = "users"
                column { it[SqLiteUser::id].varchar().primaryKey() }
                column { it[SqLiteUser::firstname].varchar().name("fname") }
                column { it[SqLiteUser::lastname].varchar().name("lname") }
                column { it[SqLiteUser::isAdmin].boolean() }
                column { it[SqLiteUser::alias].varchar() }
            }
        }

data class SqLiteUser(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: String
)
