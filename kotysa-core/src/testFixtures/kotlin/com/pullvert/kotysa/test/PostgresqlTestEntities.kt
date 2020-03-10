/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test

import com.pullvert.kotysa.tables
import java.time.*
import java.util.*

val postgresqlTables =
        tables().postgresql {
            table<PostgresqlRole> {
                name = "roles"
                column { it[PostgresqlRole::id].uuid().primaryKey() }
                column { it[PostgresqlRole::label].varchar() }
            }
            table<PostgresqlUser> {
                name = "users"
                column { it[PostgresqlUser::id].uuid().primaryKey("PK_users") }
                column { it[PostgresqlUser::firstname].varchar().name("fname") }
                column { it[PostgresqlUser::lastname].varchar().name("lname") }
                column { it[PostgresqlUser::isAdmin].boolean() }
                column { it[PostgresqlUser::roleId].uuid().foreignKey<PostgresqlRole>("FK_users_roles") }
                column { it[PostgresqlUser::alias].varchar() }
            }
//            table<PostgresqlAllTypesNotNull> {
//                name = "all_types"
//                column { it[PostgresqlAllTypesNotNull::id].uuid().primaryKey() }
//                column { it[PostgresqlAllTypesNotNull::string].varchar() }
//                column { it[PostgresqlAllTypesNotNull::boolean].boolean() }
//                column { it[PostgresqlAllTypesNotNull::localDate].date() }
//                column { it[PostgresqlAllTypesNotNull::offsetDateTime].timestampWithTimeZone() }
//                column { it[PostgresqlAllTypesNotNull::localTim].time9() }
//                column { it[PostgresqlAllTypesNotNull::localDateTime1].dateTime() }
//                column { it[PostgresqlAllTypesNotNull::localDateTime2].timestamp() }
//                column { it[PostgresqlAllTypesNotNull::uuid].uuid() }
//                column { it[PostgresqlAllTypesNotNull::int].int() }
//            }
//            table<PostgresqlAllTypesNullable> {
//                name = "all_types_nullable"
//                column { it[PostgresqlAllTypesNullable::id].uuid().primaryKey() }
//                column { it[PostgresqlAllTypesNullable::string].varchar() }
//                column { it[PostgresqlAllTypesNullable::localDate].date() }
//                column { it[PostgresqlAllTypesNullable::offsetDateTime].timestampWithTimeZone() }
//                column { it[PostgresqlAllTypesNullable::localTim].time9() }
//                column { it[PostgresqlAllTypesNullable::localDateTime1].dateTime() }
//                column { it[PostgresqlAllTypesNullable::localDateTime2].timestamp() }
//                column { it[PostgresqlAllTypesNullable::uuid].uuid() }
//                column { it[PostgresqlAllTypesNullable::int].int() }
//            }
//            table<PostgresqlAllTypesNullableDefaultValue> {
//                column { it[PostgresqlAllTypesNullableDefaultValue::id].uuid().primaryKey() }
//                column { it[PostgresqlAllTypesNullableDefaultValue::string].varchar().defaultValue("default") }
//                column { it[PostgresqlAllTypesNullableDefaultValue::localDate].date().defaultValue(LocalDate.MAX) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::offsetDateTime].timestampWithTimeZone().defaultValue(OffsetDateTime.of(2019, 11, 4, 0, 0, 0, 0, ZoneOffset.UTC)) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::localTim].time9().defaultValue(LocalTime.MAX) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::localDateTime1].dateTime().defaultValue(LocalDateTime.of(2018, 11, 4, 0, 0)) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::localDateTime2].timestamp().defaultValue(LocalDateTime.of(2019, 11, 4, 0, 0)) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::uuid].uuid().defaultValue(UUID.fromString(defaultUuid)) }
//                column { it[PostgresqlAllTypesNullableDefaultValue::int].int().defaultValue(42) }
//            }
//            table<PostgresqlUuid> {
//                column { it[PostgresqlUuid::id].uuid().primaryKey() }
//                column { it[PostgresqlUuid::roleIdNotNull].uuid().foreignKey<PostgresqlRole>() }
//                column { it[PostgresqlUuid::roleIdNullable].uuid().foreignKey<PostgresqlRole>() }
//            }
            table<PostgresqlLocalDate> {
                column { it[PostgresqlLocalDate::id].uuid().primaryKey() }
                column { it[PostgresqlLocalDate::localDateNotNull].date() }
                column { it[PostgresqlLocalDate::localDateNullable].date() }
            }
            table<PostgresqlLocalDateTime> {
                column { it[PostgresqlLocalDateTime::id].uuid().primaryKey() }
                column { it[PostgresqlLocalDateTime::localDateTimeAsTimestampNotNull].timestamp() }
                column { it[PostgresqlLocalDateTime::localDateTimeAsTimestampNullable].timestamp() }
            }
            table<PostgresqlOffsetDateTime> {
                column { it[PostgresqlOffsetDateTime::id].uuid().primaryKey() }
                column { it[PostgresqlOffsetDateTime::offsetDateTimeNotNull].timestampWithTimeZone() }
                column { it[PostgresqlOffsetDateTime::offsetDateTimeNullable].timestampWithTimeZone() }
            }
            table<PostgresqlLocalTime> {
                column { it[PostgresqlLocalTime::id].uuid().primaryKey() }
                column { it[PostgresqlLocalTime::localTimeNotNull].time() }
                column { it[PostgresqlLocalTime::localTimeNullable].time() }
            }
            table<PostgresqlInt> {
                column { it[PostgresqlInt::id].serial().primaryKey() }
                column { it[PostgresqlInt::intNotNull].integer() }
                column { it[PostgresqlInt::intNullable].integer() }
            }
        }

/**
 * @author Fred Montariol
 */
data class PostgresqlRole(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

val postgresqlUser = PostgresqlRole("user")
val postgresqlAdmin = PostgresqlRole("admin")

/**
 * @author Fred Montariol
 */
data class PostgresqlUser(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val roleId: UUID,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)

val postgresqlJdoe = PostgresqlUser("John", "Doe", false, postgresqlUser.id)
val postgresqlBboss = PostgresqlUser("Big", "Boss", true, postgresqlAdmin.id, "TheBoss")

///**
// * @author Fred Montariol
// */
//data class PostgresqlAllTypesNotNull(
//        val id: UUID,
//        val string: String,
//        val boolean: Boolean,
//        val localDate: LocalDate,
//        val offsetDateTime: OffsetDateTime,
//        val localTim: LocalTime,
//        val localDateTime1: LocalDateTime,
//        val localDateTime2: LocalDateTime,
//        val uuid: UUID,
//        val int: Int
//)
//
//val postgresqlAllTypesNotNull = PostgresqlAllTypesNotNull(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), "",
//        true, LocalDate.now(), OffsetDateTime.now(), LocalTime.now(), LocalDateTime.now(), LocalDateTime.now(),
//        UUID.randomUUID(), 1)
//
///**
// * @author Fred Montariol
// */
//data class PostgresqlAllTypesNullable(
//        val id: UUID,
//        val string: String?,
//        val localDate: LocalDate?,
//        val offsetDateTime: OffsetDateTime?,
//        val localTim: LocalTime?,
//        val localDateTime1: LocalDateTime?,
//        val localDateTime2: LocalDateTime?,
//        val uuid: UUID?,
//        val int: Int?
//)
//
//val postgresqlAllTypesNullable = PostgresqlAllTypesNullable(UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"), null,
//        null, null, null, null, null, null, null)
//
///**
// * @author Fred Montariol
// */
//data class PostgresqlAllTypesNullableDefaultValue(
//        val string: String? = null,
//        val localDate: LocalDate? = null,
//        val offsetDateTime: OffsetDateTime? = null,
//        val localTim: LocalTime? = null,
//        val localDateTime1: LocalDateTime? = null,
//        val localDateTime2: LocalDateTime? = null,
//        val uuid: UUID? = null,
//        val int: Int? = null,
//        val id: UUID = UUID.randomUUID()
//)
//
//val postgresqlAllTypesNullableDefaultValue = PostgresqlAllTypesNullableDefaultValue()
//
///**
// * @author Fred Montariol
// */
//data class PostgresqlUuid(
//        val roleIdNotNull: UUID,
//        val roleIdNullable: UUID? = null,
//        val id: UUID = UUID.randomUUID()
//)
//
//val postgresqlUuidWithNullable = PostgresqlUuid(postgresqlUser.id, postgresqlAdmin.id)
//val postgresqlUuidWithoutNullable = PostgresqlUuid(postgresqlUser.id)

/**
 * @author Fred Montariol
 */
data class PostgresqlLocalDate(
        val localDateNotNull: LocalDate,
        val localDateNullable: LocalDate? = null,
        val id: UUID = UUID.randomUUID()
)

val postgresqlLocalDateWithNullable = PostgresqlLocalDate(LocalDate.of(2019, 11, 4), LocalDate.of(2018, 11, 4))
val postgresqlLocalDateWithoutNullable = PostgresqlLocalDate(LocalDate.of(2019, 11, 6))

/**
 * @author Fred Montariol
 */
data class PostgresqlLocalDateTime(
        val localDateTimeAsTimestampNotNull: LocalDateTime,
        val localDateTimeAsTimestampNullable: LocalDateTime? = null,
        val id: UUID = UUID.randomUUID()
)

val postgresqlLocalDateTimeWithNullable = PostgresqlLocalDateTime(LocalDateTime.of(2019, 11, 4, 0, 0), LocalDateTime.of(2018, 11, 4, 0, 0))
val postgresqlLocalDateTimeWithoutNullable = PostgresqlLocalDateTime(LocalDateTime.of(2019, 11, 6, 0, 0))

/**
 * @author Fred Montariol
 */
data class PostgresqlOffsetDateTime(
        val offsetDateTimeNotNull: OffsetDateTime,
        val offsetDateTimeNullable: OffsetDateTime? = null,
        val id: UUID = UUID.randomUUID()
) {
    /**
     * This override is required because Postgresql stores **timestamp with time zone** sing server's local offset,
     * so we need to use [OffsetDateTime.isEqual] to test equality on [OffsetDateTime] fields that is based on instant
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostgresqlOffsetDateTime

        if (!offsetDateTimeNotNull.isEqual(other.offsetDateTimeNotNull)) return false
        if (offsetDateTimeNullable != null) {
            if (!offsetDateTimeNullable.isEqual(other.offsetDateTimeNullable)) return false
        } else if (other.offsetDateTimeNullable != null) {
            return false
        }
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = offsetDateTimeNotNull.hashCode()
        result = 31 * result + (offsetDateTimeNullable?.hashCode() ?: 0)
        result = 31 * result + id.hashCode()
        return result
    }
}

val postgresqlOffsetDateTimeWithNullable = PostgresqlOffsetDateTime(
        OffsetDateTime.of(2019, 11, 4, 0, 0, 0, 0, ZoneOffset.UTC),
        OffsetDateTime.of(2018, 11, 4, 0, 0, 0, 0, ZoneOffset.UTC))
val postgresqlOffsetDateTimeWithoutNullable = PostgresqlOffsetDateTime(
        OffsetDateTime.of(2019, 11, 6, 0, 0, 0, 0, ZoneOffset.UTC))

/**
 * @author Fred Montariol
 */
data class PostgresqlLocalTime(
        val localTimeNotNull: LocalTime,
        val localTimeNullable: LocalTime? = null,
        val id: UUID = UUID.randomUUID()
)

val postgresqlLocalTimeWithNullable = PostgresqlLocalTime(LocalTime.of(12, 4), LocalTime.of(11, 4))
val postgresqlLocalTimeWithoutNullable = PostgresqlLocalTime(LocalTime.of(12, 6))

/**
 * @author Fred Montariol
 */
data class PostgresqlInt(
        val intNotNull: Int,
        val intNullable: Int? = null,
        val id: Int? = null
)

val postgresqlIntWithNullable = PostgresqlInt(10, 6)
val postgresqlIntWithoutNullable = PostgresqlInt(12)
