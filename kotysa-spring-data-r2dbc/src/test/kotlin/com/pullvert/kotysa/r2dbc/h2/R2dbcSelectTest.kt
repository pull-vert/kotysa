/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.NonUniqueResultException
import com.pullvert.kotysa.count
import com.pullvert.kotysa.test.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2dbcSelectTest : AbstractR2dbcTest<UserRepositorySelect>() {
    override val context = startContext<UserRepositorySelect>()

    override val repository = getContextRepository<UserRepositorySelect>()

    @Test
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2Jdoe, h2Bboss)
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
    fun `Verify selectOneNonUnique throws NonUniqueResultException`() {
        assertThatThrownBy { repository.selectOneNonUnique().block() }
                .isInstanceOf(NonUniqueResultException::class.java)
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
}

/**
 * @author Fred Montariol
 */
class UserRepositorySelect(dbClient: DatabaseClient) : AbstractUserRepository(dbClient) {

    fun countAllUsers() = sqlClient.countAll<H2User>()

    fun countUsersWithAlias() = sqlClient.select { count { it[H2User::alias] } }.fetchOne()

    fun selectOneNonUnique() = sqlClient.select<H2User>()
            .fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    fun selectWithJoin() =
            sqlClient.select { UserWithRoleDto(it[H2User::lastname], it[H2Role::label]) }
                    .innerJoin<H2Role>().on { it[H2User::roleId] }
                    .fetchAll()
}
