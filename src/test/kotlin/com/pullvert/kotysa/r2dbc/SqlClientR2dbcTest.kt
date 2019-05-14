/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.tables
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
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

class SqlClientSelectR2DbcTest {
    val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<UserRepository>()
                }
                listener<ApplicationReadyEvent> {
                    ref<UserRepository>().init()
                }
                r2dbcH2()
            }.run()

    val repository = context.getBean<UserRepository>()

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
    fun `Verify findAllMappedToDto does the mapping`() {
        assertThat(repository.findAllMappedToDto().toIterable())
                .hasSize(2)
                .extracting("name", "alias")
                .containsExactlyInAnyOrder(
                        tuple("John Doe", null),
                        tuple("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() {
        assertThat(repository.deleteAll().block())
                .isEqualTo(2)
        assertThat(repository.findAll().toIterable())
                .isEmpty()
        // re-insert users
        repository.insert().block()
    }
}

private val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::alias].varchar() }
            }
        }

class UserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTable()
                .then(deleteAll())
                .then(insert())
                .block()
    }

    fun createTable() = sqlClient.createTable<User>()

    fun insert() = sqlClient.insert(jdoe, bboss)

    fun deleteAll() = sqlClient.deleteFromTable<User>().execute()

    fun findAll() = sqlClient.select<User>().fetchAll()

    fun count() = Mono.empty<Long>()
//			sqlClient.select<Long>("COUNT(*)")
//					.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()
}

val jdoe = User("jdoe", "John", "Doe")
val bboss = User("bboss", "Big", "Boss", "TheBoss")

data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val alias: String? = null
) {
//	val id: Int? = null // generated auto-increment
}

data class UserDto(
        val name: String,
        val alias: String?
)
