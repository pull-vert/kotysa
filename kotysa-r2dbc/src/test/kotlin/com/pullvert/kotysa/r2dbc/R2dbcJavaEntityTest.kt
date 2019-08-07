/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import com.pullvert.kotysa.test.common.UserDto
import com.pullvert.kotysa.test.common.bbossJava
import com.pullvert.kotysa.test.common.jdoeJava
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
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAll().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoeJava, bbossJava)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() {
        assertThat(repository.selectFirstByFirstame("John").block())
                .isEqualTo(jdoeJava)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify selectByAlias1 finds TheBoss`() {
        assertThat(repository.selectByAlias1("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bbossJava)
    }

    @Test
    fun `Verify selectByAlias2 finds TheBoss`() {
        assertThat(repository.selectByAlias2("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bbossJava)
    }

    @Test
    fun `Verify selectByAlias3 finds TheBoss`() {
        assertThat(repository.selectByAlias3("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(bbossJava)
    }

    @Test
    fun `Verify selectByAlias1 with null alias finds John`() {
        assertThat(repository.selectByAlias1(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoeJava)
    }

    @Test
    fun `Verify selectAllByAlias2 with null alias finds John`() {
        assertThat(repository.selectByAlias2(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoeJava)
    }

    @Test
    fun `Verify selectByAlias3 with null alias finds John`() {
        assertThat(repository.selectByAlias3(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoeJava)
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
    fun `Verify deleteAll works correctly`() {
        assertThat(repository.deleteAll().block())
                .isEqualTo(2)
        assertThat(repository.selectAll().toIterable())
                .isEmpty()
        // re-insert users
        repository.insert().block()
    }
}

private val tables =
        tables {
            table<com.pullvert.kotysa.test.common.JavaUser> {
                name = "java_users"
                column { it[com.pullvert.kotysa.test.common.JavaUser::getLogin].varchar().primaryKey }
                column { it[com.pullvert.kotysa.test.common.JavaUser::getFirstname].varchar().name("fname") }
                column { it[com.pullvert.kotysa.test.common.JavaUser::getLastname].varchar().name("lname") }
                column { it[com.pullvert.kotysa.test.common.JavaUser::isAdmin].boolean() }
                column { it[com.pullvert.kotysa.test.common.JavaUser::getAlias1].varchar() }
                column { it[com.pullvert.kotysa.test.common.JavaUser::getAlias2].varchar() }
                column { it[com.pullvert.kotysa.test.common.JavaUser::getAlias3].varchar() }
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

    fun createTable() = sqlClient.createTable<com.pullvert.kotysa.test.common.JavaUser>()

    fun insert() = sqlClient.insert(jdoeJava, bbossJava)

    fun deleteAll() = sqlClient.deleteAllFromTable<com.pullvert.kotysa.test.common.JavaUser>()

    fun selectAll() = sqlClient.selectAll<com.pullvert.kotysa.test.common.JavaUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<com.pullvert.kotysa.test.common.JavaUser>()
            .where { it[com.pullvert.kotysa.test.common.JavaUser::getFirstname] eq firstname }
            .fetchFirst()

    fun selectByAlias1(alias: String?) = sqlClient.select<com.pullvert.kotysa.test.common.JavaUser>()
            .where { it[com.pullvert.kotysa.test.common.JavaUser::getAlias1] eq alias }
            .fetchAll()

    fun selectByAlias2(alias: String?) = sqlClient.select<com.pullvert.kotysa.test.common.JavaUser>()
            .where { it[com.pullvert.kotysa.test.common.JavaUser::getAlias2] eq alias }
            .fetchAll()

    fun selectByAlias3(alias: String?) = sqlClient.select<com.pullvert.kotysa.test.common.JavaUser>()
            .where { it[com.pullvert.kotysa.test.common.JavaUser::getAlias3] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[com.pullvert.kotysa.test.common.JavaUser::getFirstname]} ${it[com.pullvert.kotysa.test.common.JavaUser::getLastname]}",
                        it[com.pullvert.kotysa.test.common.JavaUser::getAlias1])
            }.fetchAll()
}
