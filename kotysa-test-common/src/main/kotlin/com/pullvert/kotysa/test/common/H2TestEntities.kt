/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test.common

import com.pullvert.kotysa.tables
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

val h2Tables =
        tables().h2 {
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
                column { it[H2User::roleId].uuid().foreignKey<H2Role>() }
                column { it[H2User::alias].varchar() }
            }
            table<H2AllTypesNotNull> {
                name = "all_types"
                column { it[H2AllTypesNotNull::id].uuid().primaryKey }
                column { it[H2AllTypesNotNull::string].varchar() }
                column { it[H2AllTypesNotNull::boolean].boolean() }
                column { it[H2AllTypesNotNull::localDate].date() }
                column { it[H2AllTypesNotNull::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNotNull::localTim].time9() }
                column { it[H2AllTypesNotNull::localDateTime1].dateTime() }
                column { it[H2AllTypesNotNull::localDateTime2].timestamp() }
                column { it[H2AllTypesNotNull::uuid].uuid() }
            }
            table<H2AllTypesNullable> {
                name = "all_types_nullable"
                column { it[H2AllTypesNullable::id].uuid().primaryKey }
                column { it[H2AllTypesNullable::string].varchar() }
                column { it[H2AllTypesNullable::localDate].date() }
                column { it[H2AllTypesNullable::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNullable::localTim].time9() }
                column { it[H2AllTypesNullable::localDateTime1].dateTime() }
                column { it[H2AllTypesNullable::localDateTime2].timestamp() }
                column { it[H2AllTypesNullable::uuid].uuid() }
            }
            table<H2Uuid> {
                column { it[H2Uuid::id].uuid().primaryKey }
                column { it[H2Uuid::roleIdNotNull].uuid().foreignKey<H2Role>() }
                column { it[H2Uuid::roleIdNullable].uuid().foreignKey<H2Role>() }
            }
        }

/**
 * @author Fred Montariol
 */
data class H2Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

val h2User = H2Role("user")
val h2Admin = H2Role("admin")

/**
 * @author Fred Montariol
 */
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

/**
 * @author Fred Montariol
 */
data class H2AllTypesNotNull(
        val id: UUID,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val offsetDateTime: OffsetDateTime,
        val localTim: LocalTime,
        val localDateTime1: LocalDateTime,
        val localDateTime2: LocalDateTime,
        val uuid: UUID
)

/**
 * @author Fred Montariol
 */
data class H2AllTypesNullable(
        val id: UUID,
        val string: String?,
        val localDate: LocalDate?,
        val offsetDateTime: OffsetDateTime?,
        val localTim: LocalTime?,
        val localDateTime1: LocalDateTime?,
        val localDateTime2: LocalDateTime?,
        val uuid: UUID?
)

val h2AllTypesNotNull = H2AllTypesNotNull(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), "", true, LocalDate.now(), OffsetDateTime.now(), LocalTime.now(), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
val h2AllTypesNullable = H2AllTypesNullable(UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"), null, null, null, null, null, null, null)

/**
 * @author Fred Montariol
 */
data class H2Uuid(
        val roleIdNotNull: UUID,
        val roleIdNullable: UUID? = null,
        val id: UUID = UUID.randomUUID()
)

val h2UuidWithNullable = H2Uuid(h2User.id, h2Admin.id)
val h2UuidWithoutNullable = H2Uuid(h2User.id)
