/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.NoResultException
import com.pullvert.kotysa.NonUniqueResultException
import com.pullvert.kotysa.test.common.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
    fun `Verify selectAll returns all users`() = runBlockingTest {
        assertThat(repository.selectAllUsers().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2Jdoe, h2Bboss)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() = runBlockingTest {
        assertThat(repository.selectFirstByFirstame("John"))
                .isEqualTo(h2Jdoe)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() = runBlockingTest {
        assertThat(repository.selectFirstByFirstame("Unknown"))
                .isNull()
    }

    @Test
    fun `Verify selectFirstByFirstameNotNullable finds no Unknown, throws NoResultException`() {
        assertThatThrownBy {
            runBlockingTest { repository.selectFirstByFirstameNotNullable("Unknown") }
        }.isInstanceOf(NoResultException::class.java)
    }

    @Test
    fun `Verify selectOneNonUnique throws NonUniqueResultException`() {
        assertThatThrownBy {
            runBlockingTest { repository.selectOneNonUnique() }
        }.isInstanceOf(NonUniqueResultException::class.java)
    }

    @Test
    fun `Verify selectByAlias finds TheBoss`() = runBlockingTest {
        assertThat(repository.selectByAlias("TheBoss").toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Bboss)
    }

    @Test
    fun `Verify selectByAlias with null alias finds John`() = runBlockingTest {
        assertThat(repository.selectByAlias(null).toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Jdoe)
    }

    @Test
    fun `Verify selectAllMappedToDto does the mapping`() = runBlockingTest {
        assertThat(repository.selectAllMappedToDto().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() = runBlockingTest {
        assertThat(repository.deleteAllFromUsers())
                .isEqualTo(2)
        assertThat(repository.selectAllUsers().toList())
                .isEmpty()
        // re-insert users
        repository.insertUsers()
    }

    @Test
    fun `Verify updateLastname works`() = runBlockingTest {
        assertThat(repository.updateLastname("Do"))
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(h2Jdoe.firstname))
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(h2Jdoe.lastname)
    }
}

/**
 * @author Fred Montariol
 */
class CoroutinesUserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.coSqlClient(h2Tables)

    suspend fun init() = coroutineScope {
        createTables()
        deleteAllFromUsers()
        deleteAllFromRole()
        insertRoles()
        insertUsers()
    }

    suspend fun createTables() {
        sqlClient.createTable<H2Role>()
        sqlClient.createTable<H2User>()
    }

    suspend fun insertRoles() = sqlClient.insert(h2User, h2Admin)

    suspend fun insertUsers() = sqlClient.insert(h2Jdoe, h2Bboss)

    suspend fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    suspend fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<H2User>()

    fun selectAllUsers() = sqlClient.selectAll<H2User>()

    suspend fun selectFirstByFirstame(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirstOrNull()

    suspend fun selectFirstByFirstameNotNullable(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirst()

    suspend fun selectOneNonUnique() = sqlClient.select<H2User>()
            .fetchOne()

    fun selectByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    suspend fun updateLastname(newLastname: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::lastname] = newLastname }
            .where { it[H2User::id] eq h2Jdoe.id }
            .execute()
}
