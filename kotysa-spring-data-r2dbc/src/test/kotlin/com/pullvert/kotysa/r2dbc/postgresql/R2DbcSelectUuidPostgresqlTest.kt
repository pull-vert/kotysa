/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.postgresql

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import java.util.*

/**
 * @author Fred Montariol
 */
class R2DbcSelectUuidPostgresqlTest : AbstractR2dbcPostgresqlTest<UuidRepositoryPostgresqlSelect>() {
    override val context = startContext<UuidRepositoryPostgresqlSelect>()

    override val repository = getContextRepository<UuidRepositoryPostgresqlSelect>()

    @Test
    fun `Verify selectAllByRoleIdNotNull finds both results`() {
        assertThat(repository.selectAllByRoleIdNotNull(postgresqlUser.id).toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(postgresqlUuidWithNullable, postgresqlUuidWithoutNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNotNullNotEq finds no results`() {
        assertThat(repository.selectAllByRoleIdNotNullNotEq(postgresqlUser.id).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByRoleIdNullable finds postgresqlUuidWithNullable`() {
        assertThat(repository.selectAllByRoleIdNullable(postgresqlAdmin.id).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(postgresqlUuidWithNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNullable finds postgresqlUuidWithoutNullable`() {
        assertThat(repository.selectAllByRoleIdNullable(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(postgresqlUuidWithoutNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNullableNotEq finds postgresqlUuidWithoutNullable`() {
        assertThat(repository.selectAllByRoleIdNullableNotEq(postgresqlAdmin.id).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByRoleIdNullableNotEq finds no results`() {
        assertThat(repository.selectAllByRoleIdNullableNotEq(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(postgresqlUuidWithNullable)
    }
}

/**
 * @author Fred Montariol
 */
class UuidRepositoryPostgresqlSelect(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(postgresqlTables)

    override fun init() {
        createTables()
                .then(insertRoles())
                .then(insertUuids())
                .block()
    }

    override fun delete() {
        deleteAllFromUuid()
                .then(deleteAllFromRole())
                .block()
    }

    private fun createTables() =
            sqlClient.createTable<PostgresqlRole>()
                    .then(sqlClient.createTable<PostgresqlUuid>())

    private fun insertRoles() = sqlClient.insert(postgresqlUser, postgresqlAdmin)

    private fun insertUuids() = sqlClient.insert(postgresqlUuidWithNullable, postgresqlUuidWithoutNullable)

    private fun deleteAllFromRole() = sqlClient.deleteAllFromTable<PostgresqlRole>()

    private fun deleteAllFromUuid() = sqlClient.deleteAllFromTable<PostgresqlUuid>()

    fun selectAllByRoleIdNotNull(roleId: UUID) = sqlClient.select<PostgresqlUuid>()
            .where { it[PostgresqlUuid::roleIdNotNull] eq roleId }
            .fetchAll()

    fun selectAllByRoleIdNotNullNotEq(roleId: UUID) = sqlClient.select<PostgresqlUuid>()
            .where { it[PostgresqlUuid::roleIdNotNull] notEq roleId }
            .fetchAll()

    fun selectAllByRoleIdNullable(roleId: UUID?) = sqlClient.select<PostgresqlUuid>()
            .where { it[PostgresqlUuid::roleIdNullable] eq roleId }
            .fetchAll()

    fun selectAllByRoleIdNullableNotEq(roleId: UUID?) = sqlClient.select<PostgresqlUuid>()
            .where { it[PostgresqlUuid::roleIdNullable] notEq roleId }
            .fetchAll()
}
