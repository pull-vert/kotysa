/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.time.*
import java.util.*

/**
 * @author Fred Montariol
 */
class H2TablesDslTest {
    @Test
    fun `Test all supported column types for not null properties`() {
        val tables = tables().h2 {
            table<H2AllTypesNotNull> {
                name = "all_types"
                column { it[H2AllTypesNotNull::id].uuid().primaryKey() }
                column { it[H2AllTypesNotNull::string].varchar() }
                column { it[H2AllTypesNotNull::boolean].boolean() }
                column { it[H2AllTypesNotNull::localDate].date() }
                column { it[H2AllTypesNotNull::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNotNull::localTim].time9() }
                column { it[H2AllTypesNotNull::localDateTime1].dateTime() }
                column { it[H2AllTypesNotNull::localDateTime2].timestamp() }
                column { it[H2AllTypesNotNull::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, false),
                        tuple("boolean", SqlType.BOOLEAN, false),
                        tuple("localDate", SqlType.DATE, false),
                        tuple("offsetDateTime", SqlType.TIMESTAMP_WITH_TIME_ZONE, false),
                        tuple("localTim", SqlType.TIME9, false),
                        tuple("localDateTime1", SqlType.DATE_TIME, false),
                        tuple("localDateTime2", SqlType.TIMESTAMP, false),
                        tuple("uuid", SqlType.UUID, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables().h2 {
            table<H2AllTypesNullable> {
                name = "all_types_nullable"
                column { it[H2AllTypesNullable::id].uuid().primaryKey() }
                column { it[H2AllTypesNullable::string].varchar() }
                column { it[H2AllTypesNullable::localDate].date() }
                column { it[H2AllTypesNullable::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNullable::localTim].time9() }
                column { it[H2AllTypesNullable::localDateTime1].dateTime() }
                column { it[H2AllTypesNullable::localDateTime2].timestamp() }
                column { it[H2AllTypesNullable::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, true),
                        tuple("localDate", SqlType.DATE, true),
                        tuple("offsetDateTime", SqlType.TIMESTAMP_WITH_TIME_ZONE, true),
                        tuple("localTim", SqlType.TIME9, true),
                        tuple("localDateTime1", SqlType.DATE_TIME, true),
                        tuple("localDateTime2", SqlType.TIMESTAMP, true),
                        tuple("uuid", SqlType.UUID, true))
    }

    @Test
    fun `Test unnamed primary and foreign key`() {
        val tables = tables().h2 {
            table<H2Role> {
                name = "roles"
                column { it[H2Role::id].uuid().primaryKey() }
                column { it[H2Role::label].varchar() }
            }
            table<H2User> {
                name = "users"
                column { it[H2User::id].uuid().primaryKey() }
                column { it[H2User::firstname].varchar() }
                column { it[H2User::alias].varchar() }
                column { it[H2User::roleId].uuid().foreignKey<H2Role>() }
            }
        }
        assertThat(tables.allTables[H2Role::class]!!.columns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("label", SqlType.VARCHAR, false))
        val userTable = tables.allTables[H2User::class] ?: fail { "require mapped H2User" }
        assertThat(userTable.columns.values)
                .extracting("name", "sqlType", "isNullable", "fkClass", "fkName")
                .containsExactly(
                        tuple("id", SqlType.UUID, false, null, null),
                        tuple("firstname", SqlType.VARCHAR, false, null, null),
                        tuple("alias", SqlType.VARCHAR, true, null, null),
                        tuple("roleId", SqlType.UUID, false, H2Role::class, null))
        val userTablePk = userTable.primaryKey as SinglePrimaryKey<*, *>
        assertThat(userTablePk.column.entityGetter).isEqualTo(H2User::id)
        assertThat(userTablePk.name).isNull()
        val userTableFk = userTable.foreignKeys.iterator().next() as SingleForeignKey<*, *>
        assertThat(userTableFk.column.entityGetter).isEqualTo(H2User::roleId)
        assertThat(userTableFk.referencedColumn.entityGetter).isEqualTo(H2Role::id)
        assertThat(userTableFk.name).isNull()
    }

    @Test
    fun `Test named primary and foreign key`() {
        val tables = tables().h2 {
            table<H2Role> {
                name = "roles"
                column { it[H2Role::id].uuid().primaryKey() }
                column { it[H2Role::label].varchar() }
            }
            table<H2User> {
                name = "users"
                column { it[H2User::id].uuid().primaryKey("users_pk") }
                column { it[H2User::firstname].varchar() }
                column { it[H2User::alias].varchar() }
                column { it[H2User::roleId].uuid().foreignKey<H2Role>("users_fk") }
            }
        }
        assertThat(tables.allTables[H2Role::class]!!.columns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("label", SqlType.VARCHAR, false))
        val userTable = tables.allTables[H2User::class] ?: fail { "require mapped H2User" }
        assertThat(userTable.columns.values)
                .extracting("name", "sqlType", "isNullable", "fkClass", "fkName")
                .containsExactly(
                        tuple("id", SqlType.UUID, false, null, null),
                        tuple("firstname", SqlType.VARCHAR, false, null, null),
                        tuple("alias", SqlType.VARCHAR, true, null, null),
                        tuple("roleId", SqlType.UUID, false, H2Role::class, "users_fk"))
        val userTablePk = userTable.primaryKey as SinglePrimaryKey<*, *>
        assertThat(userTablePk.column.entityGetter).isEqualTo(H2User::id)
        assertThat(userTablePk.name).isEqualTo("users_pk")
        val userTableFk = userTable.foreignKeys.iterator().next() as SingleForeignKey<*, *>
        assertThat(userTableFk.column.entityGetter).isEqualTo(H2User::roleId)
        assertThat(userTableFk.referencedColumn.entityGetter).isEqualTo(H2Role::id)
        assertThat(userTableFk.name).isEqualTo("users_fk")
    }
}

/**
 * @author Fred Montariol
 */
private data class H2AllTypesNotNull(
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
private data class H2AllTypesNullable(
        val id: UUID,
        val string: String?,
        val localDate: LocalDate?,
        val offsetDateTime: OffsetDateTime?,
        val localTim: LocalTime?,
        val localDateTime1: LocalDateTime?,
        val localDateTime2: LocalDateTime?,
        val uuid: UUID?
)

/**
 * @author Fred Montariol
 */
private data class H2Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

/**
 * @author Fred Montariol
 */
private data class H2User(
        val firstname: String,
        val roleId: UUID,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)
