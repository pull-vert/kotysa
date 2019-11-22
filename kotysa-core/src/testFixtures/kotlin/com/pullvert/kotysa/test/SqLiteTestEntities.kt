/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test

import com.pullvert.kotysa.tables
import java.time.*

val sqLiteTables =
        tables().sqlite {
            table<SqLiteRole> {
                name = "roles"
                column { it[SqLiteRole::id].text().primaryKey() }
                column { it[SqLiteRole::label].text() }
            }
            table<SqLiteUser> {
                name = "users"
                column { it[SqLiteUser::id].text().primaryKey() }
                column { it[SqLiteUser::firstname].text().name("fname") }
                column { it[SqLiteUser::lastname].text().name("lname") }
                column { it[SqLiteUser::isAdmin].integer() }
                column { it[SqLiteUser::roleId].text().foreignKey<SqLiteRole>() }
                column { it[SqLiteUser::alias].text() }
            }
            table<SqLiteAllTypesNotNull> {
                name = "all_types"
                column { it[SqLiteAllTypesNotNull::id].text().primaryKey() }
                column { it[SqLiteAllTypesNotNull::string].text() }
                column { it[SqLiteAllTypesNotNull::boolean].integer() }
                column { it[SqLiteAllTypesNotNull::localDate].text() }
                column { it[SqLiteAllTypesNotNull::localDateTime].text() }
            }
            table<SqLiteAllTypesNullable> {
                name = "all_types_nullable"
                column { it[SqLiteAllTypesNullable::id].text().primaryKey() }
                column { it[SqLiteAllTypesNullable::string].text() }
                column { it[SqLiteAllTypesNullable::localDate].text() }
                column { it[SqLiteAllTypesNullable::localDateTime].text() }
            }
            table<SqLiteLocalDate> {
                column { it[SqLiteLocalDate::id].text().primaryKey() }
                column { it[SqLiteLocalDate::localDateNotNull].text() }
                column { it[SqLiteLocalDate::localDateNullable].text() }
            }
            table<SqLiteLocalDateTime> {
                column { it[SqLiteLocalDateTime::id].text().primaryKey() }
                column { it[SqLiteLocalDateTime::localDateTimeNotNull].text() }
                column { it[SqLiteLocalDateTime::localDateTimeNullable].text() }
            }
            table<SqLiteOffsetDateTime> {
                column { it[SqLiteOffsetDateTime::id].text().primaryKey() }
                column { it[SqLiteOffsetDateTime::offsetDateTimeNotNull].text() }
                column { it[SqLiteOffsetDateTime::offsetDateTimeNullable].text() }
            }
            table<SqLiteLocalTime> {
                column { it[SqLiteLocalTime::id].text().primaryKey() }
                column { it[SqLiteLocalTime::localTimeNotNull].text() }
                column { it[SqLiteLocalTime::localTimeNullable].text() }
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

/**
 * @author Fred Montariol
 */
data class SqLiteOffsetDateTime(
        val id: String,
        val offsetDateTimeNotNull: OffsetDateTime,
        val offsetDateTimeNullable: OffsetDateTime? = null
)

val sqLiteOffsetDateTimeWithNullable = SqLiteOffsetDateTime("abc",
        OffsetDateTime.of(2019, 11, 4, 0, 0, 0, 0, ZoneOffset.UTC),
        OffsetDateTime.of(2018, 11, 4, 0, 0, 0, 0, ZoneOffset.UTC))
val sqLiteOffsetDateTimeWithoutNullable = SqLiteOffsetDateTime("def",
        OffsetDateTime.of(2019, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC))

/**
 * @author Fred Montariol
 */
data class SqLiteLocalTime(
        val id: String,
        val localTimeNotNull: LocalTime,
        val localTimeNullable: LocalTime? = null
)

val sqLiteLocalTimeWithNullable = SqLiteLocalTime("abc", LocalTime.of(12, 4), LocalTime.of(11, 4))
val sqLiteLocalTimeWithoutNullable = SqLiteLocalTime("def", LocalTime.of(12, 6))
