/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.count
import com.pullvert.kotysa.tables
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author Fred Montariol
 */
class R2dbcTest {
    private val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<UserRepository>()
                }
                listener<ApplicationReadyEvent> {
                    ref<UserRepository>().init()
                }
                r2dbcH2()
            }.run()

    private val repository = context.getBean<UserRepository>()

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoeH2, bbossH2)
    }

    @Test
    fun `Verify selectAll returns all AllTypesNotNull`() {
        assertThat(repository.selectAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactly(allTypesNotNullH2)
    }

    @Disabled("waiting for https://github.com/r2dbc/r2dbc-h2/issues/78")
    @Test
    fun `Verify selectAll returns all AllTypesNullable`() {
        assertThat(repository.selectAllAllTypesNullable().toIterable())
                .hasSize(1)
                .containsExactly(allTypesNullableH2)
    }

    @Test
    fun `Verify countUsers returns 2`() {
        assertThat(repository.countAllUsers().block())
                .isEqualTo(2L)
    }

    @Test
    fun `Verify countUsers with alias returns 1`() {
        assertThat(repository.countUsersWithAlias().block())
                .isEqualTo(1L)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() {
        assertThat(repository.selectFirstByFirstame(jdoeH2.firstname).block())
                .isEqualTo(jdoeH2)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify selectByAlias finds TheBoss`() {
        assertThat(repository.selectAllByAlias(bbossH2.alias).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bbossH2)
    }

    @Test
    fun `Verify selectByAlias with null alias finds John`() {
        assertThat(repository.selectAllByAlias(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoeH2)
    }

    @Test
    fun `Verify selectAllMappedToDto does the mapping`() {
        assertThat(repository.selectAllMappedToDto().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() {
        assertThat(repository.deleteAllFromUsers().block())
                .isEqualTo(2)
        assertThat(repository.selectAllUsers().toIterable())
                .isEmpty()
        // re-insertUsers users
        repository.insertUsers().block()
    }

    @Test
    fun `Verify deleteUserById works`() {
        assertThat(repository.deleteUserById(jdoeH2.id).block())
                .isEqualTo(1)
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(1)
        // re-insertUsers jdoe
        repository.insertJDoe().block()
    }

    @Test
    fun `Verify updateLastname works`() {
        repository.updateLastname("Do").block()
        assertThat(repository.selectFirstByFirstame(jdoeH2.firstname).block())
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(jdoeH2.lastname).block()
    }

    @Test
    fun `Verify updateAlias works`() {
        repository.updateAlias("TheBigBoss").block()
        assertThat(repository.selectFirstByFirstame(bbossH2.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo("TheBigBoss")
        repository.updateAlias(null).block()
        assertThat(repository.selectFirstByFirstame(bbossH2.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo(null)
        repository.updateAlias(bbossH2.alias).block()
    }

    @Test
    fun `Verify updateAll works`() {
        val newLocalDate = LocalDate.now()
        val newInstant = Instant.now()
        val newLocalTime = LocalTime.now()
        val newLocalDateTime = LocalDateTime.now()
        val newUuid = UUID.randomUUID()
        repository.updateAllTypesNotNull("new", false, newLocalDate, newInstant, newLocalTime,
                newLocalDateTime, newLocalDateTime, newUuid).block()
        assertThat(repository.selectAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        AllTypesNotNullH2(allTypesNotNullH2.id, "new", false, newLocalDate, newInstant,
                                newLocalTime, newLocalDateTime, newLocalDateTime, newUuid))
        repository.updateAllTypesNotNull(allTypesNotNullH2.string, allTypesNotNullH2.boolean, allTypesNotNullH2.localDate,
                allTypesNotNullH2.instant, allTypesNotNullH2.localTim, allTypesNotNullH2.localDateTime1,
                allTypesNotNullH2.localDateTime2, allTypesNotNullH2.uuid).block()
    }
}

private val tables =
        tables().h2 {
            table<H2User> {
                name = "users"
                column { it[H2User::id].uuid().primaryKey }
                column { it[H2User::firstname].varchar().name("fname") }
                column { it[H2User::lastname].varchar().name("lname") }
                column { it[H2User::isAdmin].boolean() }
                column { it[H2User::alias].varchar() }
            }
            table<AllTypesNotNullH2> {
                name = "all_types"
                column { it[AllTypesNotNullH2::id].uuid().primaryKey }
                column { it[AllTypesNotNullH2::string].varchar() }
                column { it[AllTypesNotNullH2::boolean].boolean() }
                column { it[AllTypesNotNullH2::localDate].date() }
                column { it[AllTypesNotNullH2::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNullH2::localTim].time9() }
                column { it[AllTypesNotNullH2::localDateTime1].dateTime() }
                column { it[AllTypesNotNullH2::localDateTime2].timestamp() }
                column { it[AllTypesNotNullH2::uuid].uuid() }
            }
            table<AllTypesNullableH2> {
                name = "all_types_nullable"
                column { it[AllTypesNullableH2::id].uuid().primaryKey }
                column { it[AllTypesNullableH2::string].varchar() }
                column { it[AllTypesNullableH2::localDate].date() }
                column { it[AllTypesNullableH2::instant].timestampWithTimeZone() }
                column { it[AllTypesNullableH2::localTim].time9() }
                column { it[AllTypesNullableH2::localDateTime1].dateTime() }
                column { it[AllTypesNullableH2::localDateTime2].timestamp() }
                column { it[AllTypesNullableH2::uuid].uuid() }
            }
        }

/**
 * @author Fred Montariol
 */
class UserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTables()
                .then(deleteAllFromUsers())
                .then(deleteAllFromAllTypesNotNull())
                .then(deleteAllFromAllTypesNullable())
                .then(insertUsers())
                .then(insertAllTypes())
                .block()
    }

    fun createTables() =
            sqlClient.createTable<H2User>()
                    .then(sqlClient.createTable<AllTypesNotNullH2>())
                    .then(sqlClient.createTable<AllTypesNullableH2>())

    fun insertUsers() = sqlClient.insert(jdoeH2, bbossH2)

    fun insertJDoe() = sqlClient.insert(jdoeH2)

    fun insertAllTypes() = sqlClient.insert(allTypesNotNullH2, allTypesNullableH2)

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<H2User>()

    fun deleteAllFromAllTypesNotNull() = sqlClient.deleteAllFromTable<AllTypesNotNullH2>()

    fun deleteAllFromAllTypesNullable() = sqlClient.deleteAllFromTable<AllTypesNullableH2>()

    fun deleteUserById(id: UUID) = sqlClient.deleteFromTable<H2User>()
            .where { it[H2User::id] eq id }
            .execute()

    fun selectAllUsers() = sqlClient.selectAll<H2User>()

    fun countAllUsers() = sqlClient.countAll<H2User>()

    fun countUsersWithAlias() = sqlClient.select { count { it[H2User::alias] } }.fetchOne()

    fun selectAllAllTypesNotNull() = sqlClient.selectAll<AllTypesNotNullH2>()

    fun selectAllAllTypesNullable() = sqlClient.selectAll<AllTypesNullableH2>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirst()

    fun selectAllByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::lastname] = newLastname }
            .where { it[H2User::id] eq jdoeH2.id }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<H2User>()
            .set { it[H2User::alias] = newAlias }
            .where { it[H2User::id] eq bbossH2.id }
            .execute()

    fun updateAllTypesNotNull(newString: String, newBoolean: Boolean, newLocalDate: LocalDate, newInstant: Instant, newLocalTim: LocalTime,
                              newLocalDateTime1: LocalDateTime, newLocalDateTime2: LocalDateTime, newUuid: UUID) =
            sqlClient.updateTable<AllTypesNotNullH2>()
                    .set { it[AllTypesNotNullH2::string] = newString }
                    .set { it[AllTypesNotNullH2::boolean] = newBoolean }
                    .set { it[AllTypesNotNullH2::localDate] = newLocalDate }
                    .set { it[AllTypesNotNullH2::instant] = newInstant }
                    .set { it[AllTypesNotNullH2::localTim] = newLocalTim }
                    .set { it[AllTypesNotNullH2::localDateTime1] = newLocalDateTime1 }
                    .set { it[AllTypesNotNullH2::localDateTime2] = newLocalDateTime2 }
                    .set { it[AllTypesNotNullH2::uuid] = newUuid }
                    .where { it[AllTypesNotNullH2::id] eq allTypesNotNullH2.id }
                    .execute()
}
