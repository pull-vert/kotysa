/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import java.util.*

/**
 * @author Fred Montariol
 */
class R2dbcUuidSelectTest : AbstractR2dbcTest() {
    override val context = startContext<UuidRepositorySelect>()

    private val repository = getRepository<UuidRepositorySelect>()

    @Test
    fun `Verify selectAllByRoleIdNotNull finds BigBoss`() {
        assertThat(repository.selectAllByRoleIdNotNull(h2User.id).toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2UuidWithNullable, h2UuidWithoutNullable)
    }
}

/**
 * @author Fred Montariol
 */
class UuidRepositorySelect(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(h2Tables)

    override fun init() {
        createTables()
                .then(deleteAllFromUuid())
                .then(deleteAllFromRole())
                .then(insertRoles())
                .then(insertUuids())
                .block()
    }

    fun createTables() =
            sqlClient.createTable<H2Role>()
                    .then(sqlClient.createTable<H2Uuid>())

    fun insertRoles() = sqlClient.insert(h2User, h2Admin)

    fun insertUuids() = sqlClient.insert(h2UuidWithNullable, h2UuidWithoutNullable)

    fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    fun deleteAllFromUuid() = sqlClient.deleteAllFromTable<H2Uuid>()

    fun selectAllByRoleIdNotNull(roleId: UUID) = sqlClient.select<H2Uuid>()
            .where { it[H2Uuid::roleIdNotNull] eq roleId }
            .fetchAll()
}
