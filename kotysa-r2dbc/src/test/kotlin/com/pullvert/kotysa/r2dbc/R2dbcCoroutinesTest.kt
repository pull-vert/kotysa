/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
@ExperimentalCoroutinesApi
@FlowPreview
class R2DbcCoroutinesTest {
    private val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<CoroutinesUserRepository>()
                }
                listener<ApplicationReadyEvent> {
                    runBlocking {
                        ref<CoroutinesUserRepository>().init()
                    }
                }
                r2dbcH2()
            }.run()

    private val repository = context.getBean<CoroutinesUserRepository>()

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun `Verify findAll returns all users`() = runBlockingTest {
        assertThat(repository.findAll().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoe, bboss)
    }

    @Test
    fun `Verify findFirstByFirstame finds John`() = runBlockingTest {
        assertThat(repository.findFirstByFirstame("John"))
                .isEqualTo(jdoe)
    }

    @Test
    fun `Verify findFirstByFirstame finds no Unknown`() = runBlockingTest {
        assertThat(repository.findFirstByFirstame("Unknown"))
                .isNull()
    }

    @Test
    fun `Verify findByAlias finds TheBoss`() = runBlockingTest {
        assertThat(repository.findAllByAlias("TheBoss").toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(bboss)
    }

    @Test
    fun `Verify findByAlias with null alias finds John`() = runBlockingTest {
        assertThat(repository.findAllByAlias(null).toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(jdoe)
    }

    @Test
    fun `Verify findAllMappedToDto does the mapping`() = runBlockingTest {
        assertThat(repository.findAllMappedToDto().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() = runBlockingTest {
        assertThat(repository.deleteAll())
                .isEqualTo(2)
        assertThat(repository.findAll().toList())
                .isEmpty()
        // re-insertUsers users
        repository.insert()
    }

    @Test
    fun `Verify updateLastname works`() = runBlockingTest {
        repository.updateLastname("Do")
        assertThat(repository.findFirstByFirstame(jdoe.firstname))
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(jdoe.lastname)
    }
}

/**
 * @author Fred Montariol
 */
@FlowPreview
class CoroutinesUserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.coSqlClient(tables)

    suspend fun init() = coroutineScope {
        createTable()
        deleteAll()
        insert()
    }

    suspend fun createTable() = sqlClient.createTable<User>()

    suspend fun insert() = sqlClient.insert(jdoe, bboss)

    suspend fun deleteAll() = sqlClient.deleteFromTable<User>().execute()

    fun findAll() = sqlClient.select<User>().fetchAll()

    suspend fun findFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchFirstOrNull()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchAll()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    suspend fun updateLastname(newLastname: String) = sqlClient.updateTable<User>()
            .set { it[User::lastname] = newLastname }
            .where { it[User::login] eq jdoe.login }
            .execute()
}
