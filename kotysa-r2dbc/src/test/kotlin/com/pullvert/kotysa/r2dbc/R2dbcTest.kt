/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.NonUniqueResultException
import com.pullvert.kotysa.count
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
import java.util.*

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
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2Jdoe, h2Bboss)
    }

    @Test
    fun `Verify selectAll returns all AllTypesNotNull`() {
        assertThat(repository.selectAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactly(h2AllTypesNotNull)
    }

    @Disabled("waiting for https://github.com/r2dbc/r2dbc-h2/issues/78")
    @Test
    fun `Verify selectAll returns all AllTypesNullable`() {
        assertThat(repository.selectAllAllTypesNullable().toIterable())
                .hasSize(1)
                .containsExactly(h2AllTypesNullable)
    }

    @Test
    fun `Verify countUsers returns 2`() {
        assertThat(repository.countAllUsers().block()!!)
                .isEqualTo(2L)
    }

    @Test
    fun `Verify countUsers with alias returns 1`() {
        assertThat(repository.countUsersWithAlias().block()!!)
                .isEqualTo(1L)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() {
        assertThat(repository.selectFirstByFirstame(h2Jdoe.firstname).block())
                .isEqualTo(h2Jdoe)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify selectOneNonUnique throws NonUniqueResultException`() {
        assertThatThrownBy { repository.selectOneNonUnique().block() }
                .isInstanceOf(NonUniqueResultException::class.java)
    }

    @Test
    fun `Verify selectByAlias finds TheBoss`() {
        assertThat(repository.selectAllByAlias(h2Bboss.alias).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Bboss)
    }

    @Test
    fun `Verify selectByAlias with null alias finds John`() {
        assertThat(repository.selectAllByAlias(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Jdoe)
    }

    @Test
    fun `Verify selectAllMappedToDto does the mapping`() {
        assertThat(repository.selectAllMappedToDto().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("${h2Jdoe.firstname} ${h2Jdoe.lastname}", h2Jdoe.alias),
                        UserDto("${h2Bboss.firstname} ${h2Bboss.lastname}", h2Bboss.alias))
    }

    @Test
    fun `Verify selectWithJoin works correctly`() {
        assertThat(repository.selectWithJoin().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserWithRoleDto(h2Jdoe.lastname, h2User.label),
                        UserWithRoleDto(h2Bboss.lastname, h2Admin.label)
                )
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() {
        assertThat(repository.deleteAllFromUsers().block()!!)
                .isEqualTo(2)
        assertThat(repository.selectAllUsers().toIterable())
                .isEmpty()
        // re-insertUsers users
        repository.insertUsers().block()
    }

    @Test
    fun `Verify deleteUserById works`() {
        assertThat(repository.deleteUserById(h2Jdoe.id).block()!!)
                .isEqualTo(1)
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(1)
                .containsOnly(h2Bboss)
        // re-insertUsers jdoe
        repository.insertJDoe().block()
    }

    @Test
    fun `Verify deleteUserWithJoin works`() {
        assertThat(repository.deleteUserWithJoin(h2User.label).block()!!)
                .isEqualTo(1)
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(1)
                .containsOnly(h2Bboss)
        // re-insertUsers jdoe
        repository.insertJDoe().block()
    }

    @Test
    fun `Verify updateLastname works`() {
        assertThat(repository.updateLastname("Do").block()!!)
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(h2Jdoe.firstname).block())
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(h2Jdoe.lastname).block()
    }

    @Test
    fun `Verify updateWithJoin works`() {
        assertThat(repository.updateWithJoin("Do", h2User.label).block()!!)
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(h2Jdoe.firstname).block())
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(h2Jdoe.lastname).block()
    }

    @Test
    fun `Verify updateAlias works`() {
        assertThat(repository.updateAlias("TheBigBoss").block()!!)
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(h2Bboss.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo("TheBigBoss")
        assertThat(repository.updateAlias(null).block()!!)
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(h2Bboss.firstname).block())
                .extracting { user -> user?.alias }
                .isEqualTo(null)
        repository.updateAlias(h2Bboss.alias).block()
    }

    @Test
    fun `Verify updateAll works`() {
        val newLocalDate = LocalDate.now()
        val newInstant = Instant.now()
        val newLocalTime = LocalTime.now()
        val newLocalDateTime = LocalDateTime.now()
        val newUuid = UUID.randomUUID()
        repository.updateAllTypesNotNull("new", false, newLocalDate, newInstant, newLocalTime,
                newLocalDateTime, newLocalDateTime, newUuid).block()
        assertThat(repository.selectAllAllTypesNotNull().toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        H2AllTypesNotNull(h2AllTypesNotNull.id, "new", false, newLocalDate, newInstant,
                                newLocalTime, newLocalDateTime, newLocalDateTime, newUuid))
        repository.updateAllTypesNotNull(h2AllTypesNotNull.string, h2AllTypesNotNull.boolean, h2AllTypesNotNull.localDate,
                h2AllTypesNotNull.instant, h2AllTypesNotNull.localTim, h2AllTypesNotNull.localDateTime1,
                h2AllTypesNotNull.localDateTime2, h2AllTypesNotNull.uuid).block()
    }
}

/**
 * @author Fred Montariol
 */
class UserRepository(dbClient: DatabaseClient) {

    private val sqlClient = dbClient.sqlClient(h2Tables)

    fun init() {
        createTables()
                .then(deleteAllFromRole())
                .then(deleteAllFromUsers())
                .then(deleteAllFromAllTypesNotNull())
                .then(deleteAllFromAllTypesNullable())
                .then(insertRoles())
                .then(insertUsers())
                .then(insertAllTypes())
                .block()
    }

    fun createTables() =
            sqlClient.createTable<H2Role>()
                    .then(sqlClient.createTable<H2User>())
                    .then(sqlClient.createTable<H2AllTypesNotNull>())
                    .then(sqlClient.createTable<H2AllTypesNullable>())

    fun insertRoles() = sqlClient.insert(h2User, h2Admin)

    fun insertUsers() = sqlClient.insert(h2Jdoe, h2Bboss)

    fun insertJDoe() = sqlClient.insert(h2Jdoe)

    fun insertAllTypes() = sqlClient.insert(h2AllTypesNotNull, h2AllTypesNullable)

    fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<H2User>()

    fun deleteAllFromAllTypesNotNull() = sqlClient.deleteAllFromTable<H2AllTypesNotNull>()

    fun deleteAllFromAllTypesNullable() = sqlClient.deleteAllFromTable<H2AllTypesNullable>()

    fun deleteUserById(id: UUID) = sqlClient.deleteFromTable<H2User>()
            .where { it[H2User::id] eq id }
            .execute()

    fun deleteUserWithJoin(roleLabel: String) = sqlClient.deleteFromTable<H2User>()
            .innerJoinOn<H2Role> { it[H2User::roleId] }
            .where { it[H2Role::label] eq roleLabel }
            .execute()

    fun selectAllUsers() = sqlClient.selectAll<H2User>()

    fun countAllUsers() = sqlClient.countAll<H2User>()

    fun countUsersWithAlias() = sqlClient.select { count { it[H2User::alias] } }.fetchOne()

    fun selectAllAllTypesNotNull() = sqlClient.selectAll<H2AllTypesNotNull>()

    fun selectAllAllTypesNullable() = sqlClient.selectAll<H2AllTypesNullable>()

    fun selectOneNonUnique() = sqlClient.select<H2User>()
            .fetchOne()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirst()

    fun selectAllByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    fun selectWithJoin() =
            sqlClient.select { UserWithRoleDto(it[H2User::lastname], it[H2Role::label]) }
                    .innerJoinOn<H2Role> { it[H2User::roleId] }
                    .fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::lastname] = newLastname }
            .where { it[H2User::id] eq h2Jdoe.id }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<H2User>()
            .set { it[H2User::alias] = newAlias }
            .where { it[H2User::id] eq h2Bboss.id }
            .execute()

    fun updateWithJoin(newLastname: String, roleLabel: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::lastname] = newLastname }
            .innerJoinOn<H2Role> { it[H2User::roleId] }
            .where { it[H2Role::label] eq roleLabel }
            .execute()

    fun updateAllTypesNotNull(newString: String, newBoolean: Boolean, newLocalDate: LocalDate, newInstant: Instant, newLocalTim: LocalTime,
                              newLocalDateTime1: LocalDateTime, newLocalDateTime2: LocalDateTime, newUuid: UUID) =
            sqlClient.updateTable<H2AllTypesNotNull>()
                    .set { it[H2AllTypesNotNull::string] = newString }
                    .set { it[H2AllTypesNotNull::boolean] = newBoolean }
                    .set { it[H2AllTypesNotNull::localDate] = newLocalDate }
                    .set { it[H2AllTypesNotNull::instant] = newInstant }
                    .set { it[H2AllTypesNotNull::localTim] = newLocalTim }
                    .set { it[H2AllTypesNotNull::localDateTime1] = newLocalDateTime1 }
                    .set { it[H2AllTypesNotNull::localDateTime2] = newLocalDateTime2 }
                    .set { it[H2AllTypesNotNull::uuid] = newUuid }
                    .where { it[H2AllTypesNotNull::id] eq h2AllTypesNotNull.id }
                    .execute()
}
