/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
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
                .containsExactlyInAnyOrder(jdoe, bboss)
    }

    @Test
    fun `Verify selectAll returns all AllTypesNotNull`() {
        assertThat(repository.selectAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactly(allTypesNotNull)
    }

    @Disabled("waiting for https://github.com/r2dbc/r2dbc-h2/issues/78")
    @Test
    fun `Verify selectAll returns all AllTypesNullable`() {
        assertThat(repository.selectAllAllTypesNullable().toIterable())
                .hasSize(1)
                .containsExactly(allTypesNullable)
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
        assertThat(repository.selectFirstByFirstame(jdoe.firstname).block())
                .isEqualTo(jdoe)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify selectByAlias finds TheBoss`() {
        assertThat(repository.selectAllByAlias(bboss.alias).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bboss)
    }

    @Test
    fun `Verify selectByAlias with null alias finds John`() {
        assertThat(repository.selectAllByAlias(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoe)
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
        assertThat(repository.deleteUserById(jdoe.id).block())
                .isEqualTo(1)
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(1)
        // re-insertUsers jdoe
        repository.insertJDoe().block()
    }

    @Test
    fun `Verify updateLastname works`() {
        repository.updateLastname("Do").block()
        assertThat(repository.selectFirstByFirstame(jdoe.firstname).block())
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(jdoe.lastname).block()
    }

    @Test
    fun `Verify updateAlias works`() {
        repository.updateAlias("TheBigBoss").block()
        assertThat(repository.selectFirstByFirstame(bboss.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo("TheBigBoss")
        repository.updateAlias(null).block()
        assertThat(repository.selectFirstByFirstame(bboss.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo(null)
        repository.updateAlias(bboss.alias).block()
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
                        AllTypesNotNull(allTypesNotNull.id, "new", false, newLocalDate, newInstant,
                                newLocalTime, newLocalDateTime, newLocalDateTime, newUuid))
        repository.updateAllTypesNotNull(allTypesNotNull.string, allTypesNotNull.boolean, allTypesNotNull.localDate,
                allTypesNotNull.instant, allTypesNotNull.localTim, allTypesNotNull.localDateTime1,
                allTypesNotNull.localDateTime2, allTypesNotNull.uuid).block()
    }
}

private val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::id].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::id].uuid().primaryKey }
                column { it[AllTypesNotNull::string].varchar() }
                column { it[AllTypesNotNull::boolean].boolean() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNull::localTim].time9() }
                column { it[AllTypesNotNull::localDateTime1].dateTime() }
                column { it[AllTypesNotNull::localDateTime2].timestamp() }
                column { it[AllTypesNotNull::uuid].uuid() }
            }
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].uuid().primaryKey }
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestampWithTimeZone() }
                column { it[AllTypesNullable::localTim].time9() }
                column { it[AllTypesNullable::localDateTime1].dateTime() }
                column { it[AllTypesNullable::localDateTime2].timestamp() }
                column { it[AllTypesNullable::uuid].uuid() }
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
            sqlClient.createTable<User>()
                    .then(sqlClient.createTable<AllTypesNotNull>())
                    .then(sqlClient.createTable<AllTypesNullable>())

    fun insertUsers() = sqlClient.insert(jdoe, bboss)

    fun insertJDoe() = sqlClient.insert(jdoe)

    fun insertAllTypes() = sqlClient.insert(allTypesNotNull, allTypesNullable)

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<User>()

    fun deleteAllFromAllTypesNotNull() = sqlClient.deleteAllFromTable<AllTypesNotNull>()

    fun deleteAllFromAllTypesNullable() = sqlClient.deleteAllFromTable<AllTypesNullable>()

    fun deleteUserById(id: String) = sqlClient.deleteFromTable<User>()
            .where { it[User::id] eq id }
            .execute()

    fun selectAllUsers() = sqlClient.selectAll<User>()

    fun countAllUsers() = sqlClient.countAll<User>()

    fun countUsersWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun selectAllAllTypesNotNull() = sqlClient.selectAll<AllTypesNotNull>()

    fun selectAllAllTypesNullable() = sqlClient.selectAll<AllTypesNullable>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchFirst()

    fun selectAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<User>()
            .set { it[User::lastname] = newLastname }
            .where { it[User::id] eq jdoe.id }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<User>()
            .set { it[User::alias] = newAlias }
            .where { it[User::id] eq bboss.id }
            .execute()

    fun updateAllTypesNotNull(newString: String, newBoolean: Boolean, newLocalDate: LocalDate, newInstant: Instant, newLocalTim: LocalTime,
                              newLocalDateTime1: LocalDateTime, newLocalDateTime2: LocalDateTime, newUuid: UUID) =
            sqlClient.updateTable<AllTypesNotNull>()
                    .set { it[AllTypesNotNull::string] = newString }
                    .set { it[AllTypesNotNull::boolean] = newBoolean }
                    .set { it[AllTypesNotNull::localDate] = newLocalDate }
                    .set { it[AllTypesNotNull::instant] = newInstant }
                    .set { it[AllTypesNotNull::localTim] = newLocalTim }
                    .set { it[AllTypesNotNull::localDateTime1] = newLocalDateTime1 }
                    .set { it[AllTypesNotNull::localDateTime2] = newLocalDateTime2 }
                    .set { it[AllTypesNotNull::uuid] = newUuid }
                    .where { it[AllTypesNotNull::id] eq allTypesNotNull.id }
                    .execute()
}
