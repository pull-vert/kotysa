/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.test.H2User
import com.pullvert.kotysa.test.h2Bboss
import com.pullvert.kotysa.test.h2Jdoe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient


class R2DbcSelectBooleanH2Test : AbstractR2dbcH2Test<UserRepositoryH2SelectBoolean>() {
    override val context = startContext<UserRepositoryH2SelectBoolean>()

    override val repository = getContextRepository<UserRepositoryH2SelectBoolean>()

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


class UserRepositoryH2SelectBoolean(dbClient: DatabaseClient) : AbstractUserRepositoryH2(dbClient) {

    fun selectAllByIsAdminEq(value: Boolean) = sqlClient.select<H2User>()
            .where { it[H2User::isAdmin] eq value }
            .fetchAll()
}
