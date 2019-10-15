/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.test.common.H2User
import com.pullvert.kotysa.test.common.h2Bboss
import com.pullvert.kotysa.test.common.h2Jdoe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2

/**
 * @author Fred Montariol
 */
class R2dbcStringSelectTest {
    private val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<UserRepositoryStringSelect>()
                }
                listener<ApplicationReadyEvent> {
                    ref<UserRepositoryStringSelect>().init()
                }
                r2dbcH2()
            }.run()

    private val repository = context.getBean<UserRepositoryStringSelect>()

    @AfterAll
    fun afterAll() {
        context.close()
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
    fun `Verify selectByAlias finds BigBoss`() {
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
    fun `Verify selectAllByFirstameNotEq ignore John`() {
        assertThat(repository.selectAllByFirstameNotEq(h2Jdoe.firstname).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Bboss)
    }

    @Test
    fun `Verify selectAllByFirstameNotEq ignore unknow`() {
        assertThat(repository.selectAllByFirstameNotEq("Unknown").toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(h2Jdoe, h2Bboss)
    }

    @Test
    fun `Verify selectAllByAliasNotEq ignore BigBoss`() {
        assertThat(repository.selectAllByAliasNotEq(h2Bboss.alias).toIterable())
                .hasSize(0)
    }

    @Test
    fun `Verify selectAllByAliasNotEq with null alias finds BigBoss`() {
        assertThat(repository.selectAllByAliasNotEq(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Bboss)
    }

    @Test
    fun `Verify selectAllByFirstameContains get John by searching oh`() {
        assertThat(repository.selectAllByFirstameContains("oh").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Jdoe)
    }

    @Test
    fun `Verify selectAllByFirstameContains get nothing by searching boz`() {
        assertThat(repository.selectAllByFirstameContains("boz").toIterable())
                .hasSize(0)
    }

    @Test
    fun `Verify selectAllByFirstameStartsWith get John by searching Joh`() {
        assertThat(repository.selectAllByFirstameStartsWith("Joh").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Jdoe)
    }

    @Test
    fun `Verify selectAllByFirstameStartsWith get nothing by searching oh`() {
        assertThat(repository.selectAllByFirstameStartsWith("oh").toIterable())
                .hasSize(0)
    }

    @Test
    fun `Verify selectAllByFirstameEndsWith get John by searching ohn`() {
        assertThat(repository.selectAllByFirstameEndsWith("ohn").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2Jdoe)
    }

    @Test
    fun `Verify selectAllByFirstameEndsWith get nothing by searching joh`() {
        assertThat(repository.selectAllByFirstameEndsWith("joh").toIterable())
                .hasSize(0)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositoryStringSelect(dbClient: DatabaseClient) : AbstractUserRepository(dbClient) {

    fun selectAllByFirstameNotEq(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] notEq firstname }
            .fetchAll()

    fun selectAllByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            .fetchAll()

    fun selectAllByAliasNotEq(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] notEq alias }
            .fetchAll()

    fun selectAllByFirstameContains(firstnameContains: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] contains firstnameContains }
            .fetchAll()

    fun selectAllByFirstameStartsWith(firstnameStartsWith: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] startsWith firstnameStartsWith }
            .fetchAll()

    fun selectAllByFirstameEndsWith(firstnameEndsWith: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] endsWith firstnameEndsWith }
            .fetchAll()
}
