/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test.common

import com.pullvert.kotysa.tables
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

val h2Tables =
        tables().h2 {
            table<H2User> {
                name = "users"
                column { it[H2User::id].uuid().primaryKey }
                column { it[H2User::firstname].varchar().name("fname") }
                column { it[H2User::lastname].varchar().name("lname") }
                column { it[H2User::isAdmin].boolean() }
                column { it[H2User::alias].varchar() }
            }
        }

/**
 * @author Fred Montariol
 */
data class H2User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)

val jdoeH2 = H2User("John", "Doe", false)
val bbossH2 = H2User("Big", "Boss", true, "TheBoss")

val allTypesNotNullH2 = AllTypesNotNullH2(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), "", true, LocalDate.now(), Instant.now(), LocalTime.now(), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
val allTypesNullableH2 = AllTypesNullableH2(UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"), null, null, null, null, null, null, null)

/**
 * @author Fred Montariol
 */
data class AllTypesNotNullH2(
        val id: UUID,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val instant: Instant,
        val localTim: LocalTime,
        val localDateTime1: LocalDateTime,
        val localDateTime2: LocalDateTime,
        val uuid: UUID
)

/**
 * @author Fred Montariol
 */
data class AllTypesNullableH2(
        val id: UUID,
        val string: String?,
        val localDate: LocalDate?,
        val instant: Instant?,
        val localTim: LocalTime?,
        val localDateTime1: LocalDateTime?,
        val localDateTime2: LocalDateTime?,
        val uuid: UUID?
)
