/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.postgresql

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
class R2DbcSelectPostgresqlTest : AbstractR2dbcPostgresqlTest<UserRepositoryPostgresqlSelect>() {
    override val context = startContext<UserRepositoryPostgresqlSelect>()

    override val repository = getContextRepository<UserRepositoryPostgresqlSelect>()

    @Test
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAllUsers().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(postgresqlJdoe, postgresqlBboss)
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
                        UserDto("${postgresqlJdoe.firstname} ${postgresqlJdoe.lastname}", postgresqlJdoe.alias),
                        UserDto("${postgresqlBboss.firstname} ${postgresqlBboss.lastname}", postgresqlBboss.alias))
    }

    @Test
    fun `Verify selectWithJoin works correctly`() {
        assertThat(repository.selectWithJoin().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserWithRoleDto(postgresqlJdoe.lastname, postgresqlUser.label),
                        UserWithRoleDto(postgresqlBboss.lastname, postgresqlAdmin.label)
                )
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositoryPostgresqlSelect(dbClient: DatabaseClient) : AbstractUserRepositoryPostgresql(dbClient) {

    fun countAllUsers() = sqlClient.countAll<PostgresqlUser>()

    fun countUsersWithAlias() = sqlClient.select { count { it[PostgresqlUser::alias] } }.fetchOne()

    fun selectOneNonUnique() = sqlClient.select<PostgresqlUser>()
            .fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[PostgresqlUser::firstname]} ${it[PostgresqlUser::lastname]}",
                        it[PostgresqlUser::alias])
            }.fetchAll()

    fun selectWithJoin() =
            sqlClient.select { UserWithRoleDto(it[PostgresqlUser::lastname], it[PostgresqlRole::label]) }
                    .innerJoin<PostgresqlRole>().on { it[PostgresqlUser::roleId] }
                    .fetchAll()
}
