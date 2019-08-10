/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test.common

import com.pullvert.kotysa.tables
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val sqLiteTables =
        tables().sqlite {
            table<SqLiteUser> {
                name = "users"
                column { it[SqLiteUser::id].varchar().primaryKey }
                column { it[SqLiteUser::firstname].varchar().name("fname") }
                column { it[SqLiteUser::lastname].varchar().name("lname") }
                column { it[SqLiteUser::isAdmin].boolean() }
                column { it[SqLiteUser::alias].varchar() }
            }
        }

/**
 * @author Fred Montariol
 */
data class SqLiteUser(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: String
)

val jdoeSqLite = SqLiteUser("John", "Doe", false, id = "abc")
val bbossSqLite = SqLiteUser("Big", "Boss", true, "TheBoss", "def")

val allTypesNotNullSqLite = AllTypesNotNullSqLite("abc", "", true, LocalDate.now(), Instant.now(), LocalTime.now(), LocalDateTime.now())
val allTypesNullableSqLite = AllTypesNullableSqLite("def", null, null, null, null, null)

/**
 * @author Fred Montariol
 */
data class AllTypesNotNullSqLite(
        val id: String,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val instant: Instant,
        val localTim: LocalTime,
        val localDateTime: LocalDateTime
)

/**
 * @author Fred Montariol
 */
data class AllTypesNullableSqLite(
        val id: String,
        val string: String?,
        val localDate: LocalDate?,
        val instant: Instant?,
        val localTim: LocalTime?,
        val localDateTime: LocalDateTime?
)
