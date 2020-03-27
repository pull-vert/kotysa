/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2DbcSelectOrH2Test : AbstractR2dbcH2Test<UserRepositoryH2SelectOr>() {
    override val context = startContext<UserRepositoryH2SelectOr>()

    override val repository = getContextRepository<UserRepositoryH2SelectOr>()

    @Test
    fun `Verify selectRolesByLabels finds h2Admin and h2God`() {
        assertThat(repository.selectRolesByLabels(h2Admin.label, h2God.label).toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2Admin, h2God)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositoryH2SelectOr(dbClient: DatabaseClient) : AbstractUserRepositoryH2(dbClient) {

    fun selectRolesByLabels(label1: String, label2: String) = sqlClient.select<H2Role>()
            .where { it[H2Role::label] eq label1 }
            .or { it[H2Role::label] eq label2 }
            .fetchAll()
}
