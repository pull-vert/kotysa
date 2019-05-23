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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

/**
 * @author Fred Montariol
 */
@ExperimentalCoroutinesApi
@FlowPreview
class SqlClientSelectR2DbcCoroutinesTest {
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
        expectThat(repository.findAll().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(jdoe, bboss)
    }

    @Disabled("count test is disabled : See https://github.com/spring-projects/spring-fu/issues/160")
    @Test
    fun `Verify count returns expected size`() = runBlockingTest {
        expectThat(repository.count())
                .isEqualTo(2)
    }

    @Test
    fun `Verify findFirstByFirstame finds John`() = runBlockingTest {
        expectThat(repository.findFirstByFirstame("John"))
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
        expectThat(repository.findAllMappedToDto().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() = runBlockingTest {
        expectThat(repository.deleteAll())
                .isEqualTo(2)
        assertThat(repository.findAll().toList())
                .isEmpty()
        // re-insertUsers users
        repository.insert()
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
        }

/**
 * @author Fred Montariol
 */
@FlowPreview
class CoroutinesUserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(tables)

    suspend fun init() = coroutineScope {
        createTable()
        deleteAll()
        insert()
    }

    suspend fun createTable() = sqlClient.awaitCreateTable<User>()

    suspend fun insert() = sqlClient.awaitInsert(jdoe, bboss)

    suspend fun deleteAll() = sqlClient.deleteFromTable<User>().awaitExecute()

    fun findAll() = sqlClient.select<User>().fetchFlow()

    suspend fun findFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchAwaitFirstOrNull()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchFlow()

    suspend fun count() = 2
//			sqlClient.select<Long>("COUNT(*)")
//					.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchFlow()
}
