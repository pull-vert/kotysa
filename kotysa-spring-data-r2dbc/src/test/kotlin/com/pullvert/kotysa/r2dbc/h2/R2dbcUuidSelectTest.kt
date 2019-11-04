/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

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
class R2dbcUuidSelectTest : AbstractR2dbcTest<UuidRepositorySelect>() {
    override val context = startContext<UuidRepositorySelect>()

    override val repository = getContextRepository<UuidRepositorySelect>()

    @Test
    fun `Verify selectAllByRoleIdNotNull finds both results`() {
        assertThat(repository.selectAllByRoleIdNotNull(h2User.id).toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2UuidWithNullable, h2UuidWithoutNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNotNullNotEq finds nothing`() {
        assertThat(repository.selectAllByRoleIdNotNullNotEq(h2User.id).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByRoleIdNullable finds h2UuidWithNullable`() {
        assertThat(repository.selectAllByRoleIdNullable(h2Admin.id).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2UuidWithNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNullable finds h2UuidWithoutNullable`() {
        assertThat(repository.selectAllByRoleIdNullable(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2UuidWithoutNullable)
    }

    @Test
    fun `Verify selectAllByRoleIdNullableNotEq finds h2UuidWithoutNullable`() {
        assertThat(repository.selectAllByRoleIdNullableNotEq(h2Admin.id).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByRoleIdNullableNotEq finds h2UuidWithNullable`() {
        assertThat(repository.selectAllByRoleIdNullableNotEq(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2UuidWithNullable)
    }
}

/**
 * @author Fred Montariol
 */
class UuidRepositorySelect(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(h2Tables)

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
            sqlClient.createTable<H2Role>()
                    .then(sqlClient.createTable<H2Uuid>())

    private fun insertRoles() = sqlClient.insert(h2User, h2Admin)

    private fun insertUuids() = sqlClient.insert(h2UuidWithNullable, h2UuidWithoutNullable)

    private fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    private fun deleteAllFromUuid() = sqlClient.deleteAllFromTable<H2Uuid>()

    fun selectAllByRoleIdNotNull(roleId: UUID) = sqlClient.select<H2Uuid>()
            .where { it[H2Uuid::roleIdNotNull] eq roleId }
            .fetchAll()

    fun selectAllByRoleIdNotNullNotEq(roleId: UUID) = sqlClient.select<H2Uuid>()
            .where { it[H2Uuid::roleIdNotNull] notEq roleId }
            .fetchAll()

    fun selectAllByRoleIdNullable(roleId: UUID?) = sqlClient.select<H2Uuid>()
            .where { it[H2Uuid::roleIdNullable] eq roleId }
            .fetchAll()

    fun selectAllByRoleIdNullableNotEq(roleId: UUID?) = sqlClient.select<H2Uuid>()
            .where { it[H2Uuid::roleIdNullable] notEq roleId }
            .fetchAll()
}
