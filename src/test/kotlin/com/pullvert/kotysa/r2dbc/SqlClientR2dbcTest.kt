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
import reactor.core.publisher.Mono

/**
 * @author Fred Montariol
 */
class SqlClientSelectR2DbcTest {
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
        assertThat(repository.findAll().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoe, bboss)
    }

    @Disabled("count test is disabled : See https://github.com/spring-projects/spring-fu/issues/160")
    @Test
    fun `Verify count returns expected size`() {
        assertThat(repository.count().block())
                .isEqualTo(2)
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
        assertThat(repository.findAll().toIterable())
                .isEmpty()
        // re-insertUsers users
        repository.insertUsers().block()
    }
}

private val tables =
        tables().h2 {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::alias].varchar() }
            }
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::string].varchar().primaryKey }
                column { it[AllTypesNotNull::localDateTime].dateTime() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestamp() }
                column { it[AllTypesNotNull::localTim].time() }
            }
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].varchar().primaryKey } // required
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDateTime].dateTime() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestamp() }
                column { it[AllTypesNullable::localTim].time() }
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

    fun insertAllTypes() = sqlClient.insert(allTypesNotNull, allTypesNullable)

    fun deleteAllFromUsers() = sqlClient.deleteFromTable<User>().execute()

    fun deleteAllFromAllTypesNotNull() = sqlClient.deleteFromTable<AllTypesNotNull>().execute()

    fun deleteAllFromAllTypesNullable() = sqlClient.deleteFromTable<AllTypesNullable>().execute()

    fun findAll() = sqlClient.select<User>().fetchAll()

    fun findFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchFirst()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchAll()

    fun count() = Mono.empty<Long>()
//			sqlClient.select<Long>("COUNT(*)")
//					.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()
}
