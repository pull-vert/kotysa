/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.test.H2User
import com.pullvert.kotysa.test.h2Bboss
import com.pullvert.kotysa.test.h2Jdoe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2dbcSelectBooleanTest : AbstractR2dbcTest<UserRepositorySelectBoolean>() {
    override val context = startContext<UserRepositorySelectBoolean>()

    override val repository = getContextRepository<UserRepositorySelectBoolean>()

    @Test
    fun `Verify selectAllByIsAdminEq true finds Big Boss`() {
        assertThat(repository.selectAllByIsAdminEq(true).toIterable())
                .hasSize(1)
                .containsExactly(h2Bboss)
    }

    @Test
    fun `Verify selectAllByIsAdminEq false finds John`() {
        assertThat(repository.selectAllByIsAdminEq(false).toIterable())
                .hasSize(1)
                .containsExactly(h2Jdoe)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositorySelectBoolean(dbClient: DatabaseClient) : AbstractUserRepository(dbClient) {

    fun selectAllByIsAdminEq(value: Boolean) = sqlClient.select<H2User>()
            .where { it[H2User::isAdmin] eq value }
            .fetchAll()
}
