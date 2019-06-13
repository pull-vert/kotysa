/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
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
    fun `Verify findAll returns all users`() {
        assertThat(repository.findAllUsers().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoe, bboss)
    }

    @Test
    fun `Verify findAll returns all AllTypesNotNull`() {
        assertThat(repository.findAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactly(allTypesNotNull)
    }

    @Disabled("waiting for https://github.com/r2dbc/r2dbc-h2/issues/78")
    @Test
    fun `Verify findAll returns all AllTypesNullable`() {
        assertThat(repository.findAllAllTypesNullable().toIterable())
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
    fun `Verify findFirstByFirstame finds John`() {
        assertThat(repository.findFirstByFirstame(jdoe.firstname).block())
                .isEqualTo(jdoe)
    }

    @Test
    fun `Verify findFirstByFirstame finds no Unknown`() {
        assertThat(repository.findFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify findByAlias finds TheBoss`() {
        assertThat(repository.findAllByAlias(bboss.alias).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bboss)
    }

    @Test
    fun `Verify findByAlias with null alias finds John`() {
        assertThat(repository.findAllByAlias(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoe)
    }

    @Test
    fun `Verify findAllMappedToDto does the mapping`() {
        assertThat(repository.findAllMappedToDto().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() {
        assertThat(repository.deleteAllFromUsers().block())
                .isEqualTo(2)
        assertThat(repository.findAllUsers().toIterable())
                .isEmpty()
        // re-insertUsers users
        repository.insertUsers().block()
    }

    @Test
    fun `Verify deleteUserById works`() {
        assertThat(repository.deleteUserById(jdoe.login).block())
                .isEqualTo(1)
        assertThat(repository.findAllUsers().toIterable())
                .hasSize(1)
        // re-insertUsers jdoe
        repository.insertJDoe().block()
    }

    @Test
    fun `Verify updateLastname works`() {
        repository.updateLastname("Do").block()
        assertThat(repository.findFirstByFirstame(jdoe.firstname).block())
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(jdoe.lastname).block()
    }

    @Test
    fun `Verify updateAlias works`() {
        repository.updateAlias("TheBigBoss").block()
        assertThat(repository.findFirstByFirstame(bboss.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo("TheBigBoss")
        repository.updateAlias(null).block()
        assertThat(repository.findFirstByFirstame(bboss.firstname).block())
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
        repository.updateAll("new", false, newLocalDate, newInstant, newLocalTime, newLocalDateTime,
                newLocalDateTime).block()
        assertThat(repository.findAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        AllTypesNotNull(allTypesNotNull.id, "new", false, newLocalDate, newInstant,
                                newLocalTime, newLocalDateTime, newLocalDateTime))
        repository.updateAll(allTypesNotNull.string, allTypesNotNull.boolean, allTypesNotNull.localDate, allTypesNotNull.instant,
                allTypesNotNull.localTim, allTypesNotNull.localDateTime1, allTypesNotNull.localDateTime2).block()
    }
}

private val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::id].varchar().primaryKey }
                column { it[AllTypesNotNull::string].varchar() }
                column { it[AllTypesNotNull::boolean].boolean() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNull::localTim].time9() }
                column { it[AllTypesNotNull::localDateTime1].dateTime() }
                column { it[AllTypesNotNull::localDateTime2].timestamp() }
            }
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].varchar().primaryKey }
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestampWithTimeZone() }
                column { it[AllTypesNullable::localTim].time9() }
                column { it[AllTypesNullable::localDateTime1].dateTime() }
                column { it[AllTypesNullable::localDateTime2].timestamp() }
            }
        }

/**
 * @author Fred Montariol
 */
class UserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTable()
                .then(createTables())
                .then(deleteAllFromUsers())
                .then(deleteAllFromAllTypesNotNull())
                .then(deleteAllFromAllTypesNullable())
                .then(insertUsers())
                .then(insertAllTypes())
                .block()
    }

    fun createTable() = sqlClient.createTable<User>()

    fun createTables() = sqlClient.createTables(AllTypesNotNull::class, AllTypesNullable::class)

    fun insertUsers() = sqlClient.insert(jdoe, bboss)

    fun insertJDoe() = sqlClient.insert(jdoe)

    fun insertAllTypes() = sqlClient.insert(allTypesNotNull, allTypesNullable)

    fun deleteAllFromUsers() = sqlClient.deleteFromTable<User>().execute()

    fun deleteAllFromAllTypesNotNull() = sqlClient.deleteFromTable<AllTypesNotNull>().execute()

    fun deleteAllFromAllTypesNullable() = sqlClient.deleteFromTable<AllTypesNullable>().execute()

    fun deleteUserById(id: String) = sqlClient.deleteFromTable<User>()
            .where { it[User::login] eq id }
            .execute()

    fun findAllUsers() = sqlClient.select<User>().fetchAll()

    fun countAllUsers() = sqlClient.select { count<User>() }.fetchOne()

    fun countUsersWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun findAllAllTypesNotNull() = sqlClient.select<AllTypesNotNull>().fetchAll()

    fun findAllAllTypesNullable() = sqlClient.select<AllTypesNullable>().fetchAll()

    fun findFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchFirst()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchAll()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<User>()
            .set { it[User::lastname] = newLastname }
            .where { it[User::login] eq jdoe.login }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<User>()
            .set { it[User::alias] = newAlias }
            .where { it[User::login] eq bboss.login }
            .execute()

    fun updateAll(newString: String, newBoolean: Boolean, newLocalDate: LocalDate, newInstant: Instant, newLocalTim: LocalTime,
                  newLocalDateTime1: LocalDateTime, newLocalDateTime2: LocalDateTime) =
            sqlClient.updateTable<AllTypesNotNull>()
                    .set { it[AllTypesNotNull::string] = newString }
                    .set { it[AllTypesNotNull::boolean] = newBoolean }
                    .set { it[AllTypesNotNull::localDate] = newLocalDate }
                    .set { it[AllTypesNotNull::instant] = newInstant }
                    .set { it[AllTypesNotNull::localTim] = newLocalTim }
                    .set { it[AllTypesNotNull::localDateTime1] = newLocalDateTime1 }
                    .set { it[AllTypesNotNull::localDateTime2] = newLocalDateTime2 }
                    .where { it[AllTypesNotNull::id] eq allTypesNotNull.id }
                    .execute()
}
