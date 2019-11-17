/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test

import com.pullvert.kotysa.tables
import java.time.LocalDate
import java.time.LocalDateTime

val sqLiteTables =
        tables().sqlite {
            table<SqLiteRole> {
                name = "roles"
                column { it[SqLiteRole::id].varchar().primaryKey() }
                column { it[SqLiteRole::label].varchar() }
            }
            table<SqLiteUser> {
                name = "users"
                column { it[SqLiteUser::id].varchar().primaryKey() }
                column { it[SqLiteUser::firstname].varchar().name("fname") }
                column { it[SqLiteUser::lastname].varchar().name("lname") }
                column { it[SqLiteUser::isAdmin].boolean() }
                column { it[SqLiteUser::roleId].varchar().foreignKey<SqLiteRole>() }
                column { it[SqLiteUser::alias].varchar() }
            }
            table<SqLiteAllTypesNotNull> {
                name = "all_types"
                column { it[SqLiteAllTypesNotNull::id].varchar().primaryKey() }
                column { it[SqLiteAllTypesNotNull::string].varchar() }
                column { it[SqLiteAllTypesNotNull::boolean].boolean() }
                column { it[SqLiteAllTypesNotNull::localDate].date() }
                column { it[SqLiteAllTypesNotNull::localDateTime].dateTime() }
            }
            table<SqLiteAllTypesNullable> {
                name = "all_types_nullable"
                column { it[SqLiteAllTypesNullable::id].varchar().primaryKey() }
                column { it[SqLiteAllTypesNullable::string].varchar() }
                column { it[SqLiteAllTypesNullable::localDate].date() }
                column { it[SqLiteAllTypesNullable::localDateTime].dateTime() }
            }
            table<SqLiteLocalDate> {
                column { it[SqLiteLocalDate::id].varchar().primaryKey() }
                column { it[SqLiteLocalDate::localDateNotNull].date() }
                column { it[SqLiteLocalDate::localDateNullable].date() }
            }
            table<SqLiteLocalDateTime> {
                column { it[SqLiteLocalDateTime::id].varchar().primaryKey() }
                column { it[SqLiteLocalDateTime::localDateTimeNotNull].dateTime() }
                column { it[SqLiteLocalDateTime::localDateTimeNullable].dateTime() }
            }
        }

/**
 * @author Fred Montariol
 */
data class SqLiteRole(
        val label: String,
        val id: String
)

val sqLiteUser = SqLiteRole("user", "ghi")
val sqLiteAdmin = SqLiteRole("admin", "jkl")

/**
 * @author Fred Montariol
 */
data class SqLiteUser(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val roleId: String,
        val alias: String? = null,
        val id: String
)

val sqLiteJdoe = SqLiteUser("John", "Doe", false, sqLiteUser.id, id = "abc")
val sqLiteBboss = SqLiteUser("Big", "Boss", true, sqLiteAdmin.id, "TheBoss", "def")

/**
 * @author Fred Montariol
 */
data class SqLiteAllTypesNotNull(
        val id: String,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val localDateTime: LocalDateTime
)

/**
 * @author Fred Montariol
 */
data class SqLiteAllTypesNullable(
        val id: String,
        val string: String?,
        val localDate: LocalDate?,
        val localDateTime: LocalDateTime?
)

val allTypesNotNullSqLite = SqLiteAllTypesNotNull("abc", "", true, LocalDate.now(), LocalDateTime.now())
val allTypesNullableSqLite = SqLiteAllTypesNullable("def", null, null, null)

/**
 * @author Fred Montariol
 */
data class SqLiteLocalDate(
        val id: String,
        val localDateNotNull: LocalDate,
        val localDateNullable: LocalDate? = null
)

val sqLiteLocalDateWithNullable = SqLiteLocalDate("abc", LocalDate.of(2019, 11, 4), LocalDate.of(2018, 11, 4))
val sqLiteLocalDateWithoutNullable = SqLiteLocalDate("def", LocalDate.of(2019, 11, 6))

/**
 * @author Fred Montariol
 */
data class SqLiteLocalDateTime(
        val id: String,
        val localDateTimeNotNull: LocalDateTime,
        val localDateTimeNullable: LocalDateTime? = null
)

val sqLiteLocalDateTimeWithNullable = SqLiteLocalDateTime("abc", LocalDateTime.of(2019, 11, 4, 0, 0), LocalDateTime.of(2018, 11, 4, 0, 0))
val sqLiteLocalDateTimeWithoutNullable = SqLiteLocalDateTime("def", LocalDateTime.of(2019, 11, 6, 0, 0))

