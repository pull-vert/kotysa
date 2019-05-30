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
    fun `Verify findFirstByFirstame finds John`() {
        assertThat(repository.findFirstByFirstame("John").block())
                .isEqualTo(jdoe)
    }

    @Test
    fun `Verify findFirstByFirstame finds no Unknown`() {
        assertThat(repository.findFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify findByAlias finds TheBoss`() {
        assertThat(repository.findAllByAlias("TheBoss").toIterable())
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
}

private val tables =
        tables().h2 {
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
                column { it[AllTypesNotNull::string].varchar().primaryKey }
                column { it[AllTypesNotNull::boolean].boolean() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNull::localTim].time9() }
                column { it[AllTypesNotNull::localDateTime1].dateTime() }
                column { it[AllTypesNotNull::localDateTime2].timestamp() }
            }
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].varchar().primaryKey } // required
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
}
