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
                    bean<InitR2dbcRepository>()
                    bean<TestRepository>()
                }
                listener<ApplicationReadyEvent> {
                    ref<TestRepository>().init1()
                    ref<InitR2dbcRepository>().init2()
                    ref<TestRepository>().init3()
                }
                r2dbcH2()
            }.run()

    val repository = context.getBean<TestRepository>()

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun `Verify findAll returns all users`() {
        assertThat(repository.findAll().toIterable())
                .hasSize(3)
                .containsExactlyInAnyOrder(smaldini, sdeleuze, bclozel)
    }

    @Disabled("count test is disabled : See https://github.com/spring-projects/spring-fu/issues/160")
    @Test
    fun `Verify count returns expected size`() {
        assertThat(repository.count().block())
                .isEqualTo(3)
    }

    @Test
    fun `Verify findAllMappedToDto does the mapping`() {
        assertThat(repository.findAllMappedToDto().toIterable())
                .hasSize(3)
                .extracting("name", "optional")
                .containsExactlyInAnyOrder(
                        tuple("Stéphane Maldini", null),
                        tuple("Sébastien Deleuze", "hasOptional"),
                        tuple("Brian Clozel", null))
    }
}

val smaldini = User("smaldini", "Stéphane", "Maldini")
val sdeleuze = User("sdeleuze", "Sébastien", "Deleuze", "hasOptional")
val bclozel = User("bclozel", "Brian", "Clozel")

class InitR2dbcRepository(private val client: DatabaseClient) {

    fun init2() {
        deleteAll()
                .block()
    }

    fun deleteAll() = client.execute().sql("DELETE FROM users").fetch().one().then()
}

private val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::optional].varchar() }
            }
        }

class TestRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init1() {
        sqlClient.createTables()
                .block()
    }

    fun init3() {
        sqlClient.insert(smaldini, sdeleuze, bclozel)
                .block()
    }

    fun findAll() = sqlClient.select<User>().fetchAll()

    fun count() = Mono.empty<Long>()
//			sqlClient.select<Long>("COUNT(*)")
//					.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::optional])
            }.fetchAll()
}

data class StrictUser(
        val login: String,
        val fname: String,
        val lname: String,
        val optional: String? = null
)

data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val optional: String? = null
) {
//	val id: Int? = null // generated auto-increment
}

data class UserDto(
        val name: String,
        val optional: String?
)
