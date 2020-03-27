/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.postgresql

import com.pullvert.kotysa.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2DbcSelectOrPostgresqlTest : AbstractR2dbcPostgresqlTest<UserRepositoryPostgresqlSelectOr>() {
    override val context = startContext<UserRepositoryPostgresqlSelectOr>()

    override val repository = getContextRepository<UserRepositoryPostgresqlSelectOr>()

    @Test
    fun `Verify selectRolesByLabels finds postgresqlAdmin and postgresqlGod`() {
        assertThat(repository.selectRolesByLabels(postgresqlAdmin.label, postgresqlGod.label).toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(postgresqlAdmin, postgresqlGod)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositoryPostgresqlSelectOr(dbClient: DatabaseClient) : AbstractUserRepositoryPostgresql(dbClient) {

    fun selectRolesByLabels(label1: String, label2: String) = sqlClient.select<PostgresqlRole>()
            .where { it[PostgresqlRole::label] eq label1 }
            .or { it[PostgresqlRole::label] eq label2 }
            .fetchAll()
}
