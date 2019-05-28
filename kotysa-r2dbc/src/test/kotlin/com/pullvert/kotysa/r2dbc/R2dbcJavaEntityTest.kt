/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
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
class R2dbcJavaEntityTest {
    private val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<JavaUserRepository>()
                }
                listener<ApplicationReadyEvent> {
                    ref<JavaUserRepository>().init()
                }
                r2dbcH2()
            }.run()

    private val repository = context.getBean<JavaUserRepository>()

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun `Verify findAll returns all users`() {
        assertThat(repository.findAll().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoeJava, bbossJava)
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
                .containsExactlyInAnyOrder(bbossJava)
    }

    @Test
    fun `Verify findByAlias with null alias finds John`() {
        assertThat(repository.findAllByAlias(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoeJava)
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
    fun `Verify deleteAll works correctly`() {
        assertThat(repository.deleteAll().block())
                .isEqualTo(2)
        assertThat(repository.findAll().toIterable())
                .isEmpty()
        // re-insert users
        repository.insert().block()
    }
}

private val tables =
        tables().h2 {
            table<JavaUser> {
                name = "java_users"
                column { it[JavaUser::getLogin].varchar().primaryKey }
                column { it[JavaUser::getFirstname].varchar().name("fname") }
                column { it[JavaUser::getLastname].varchar().name("lname") }
                column { it[JavaUser::isAdmin].boolean() }
                column { it[JavaUser::getAlias].varchar() }
            }
        }

/**
 * @author Fred Montariol
 */
class JavaUserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTable()
                .then(deleteAll())
                .then(insert())
                .block()
    }

    fun createTable() = sqlClient.createTable<JavaUser>()

    fun insert() = sqlClient.insert(jdoeJava, bbossJava)

    fun deleteAll() = sqlClient.deleteFromTable<JavaUser>().execute()

    fun findAll() = sqlClient.select<JavaUser>().fetchAll()

    fun findFirstByFirstame(firstname: String) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getFirstname] eq firstname }
            .fetchFirst()

    fun findAllByAlias(alias: String?) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getAlias] eq alias }
            .fetchAll()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[JavaUser::getFirstname]} ${it[JavaUser::getLastname]}",
                        it[JavaUser::getAlias])
            }.fetchAll()
}
