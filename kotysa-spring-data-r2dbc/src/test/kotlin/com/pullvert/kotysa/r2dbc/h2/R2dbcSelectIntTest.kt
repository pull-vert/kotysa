/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.H2Int
import com.pullvert.kotysa.test.h2IntWithNullable
import com.pullvert.kotysa.test.h2IntWithoutNullable
import com.pullvert.kotysa.test.h2Tables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2dbcSelectIntTest : AbstractR2dbcTest<IntRepositorySelect>() {
    override val context = startContext<IntRepositorySelect>()

    override val repository = getContextRepository<IntRepositorySelect>()

    @Test
    fun `Verify selectAllByIntNotNull finds h2IntWithNullable`() {
        assertThat(repository.selectAllByIntNotNull(10).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullNotEq finds h2IntWithoutNullable`() {
        assertThat(repository.selectAllByIntNotNullNotEq(10).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithoutNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullInf finds h2IntWithNullable`() {
        assertThat(repository.selectAllByIntNotNullInf(11).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullInf finds no results when equals`() {
        assertThat(repository.selectAllByIntNotNullInf(10).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByIntNotNullInfOrEq finds h2IntWithNullable`() {
        assertThat(repository.selectAllByIntNotNullInfOrEq(11).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullInfOrEq finds h2IntWithNullable when equals`() {
        assertThat(repository.selectAllByIntNotNullInfOrEq(10).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullSup finds h2IntWithoutNullable`() {
        assertThat(repository.selectAllByIntNotNullSup(11).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithoutNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullSup finds no results when equals`() {
        assertThat(repository.selectAllByIntNotNullSup(12).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByIntNotNullSupOrEq finds h2IntWithoutNullable`() {
        assertThat(repository.selectAllByIntNotNullSupOrEq(11).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithoutNullable)
    }

    @Test
    fun `Verify selectAllByIntNotNullSupOrEq finds h2IntWithoutNullable when equals`() {
        assertThat(repository.selectAllByIntNotNullSupOrEq(12).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithoutNullable)
    }

    @Test
    fun `Verify selectAllByIntNullable finds h2UuidWithNullable`() {
        assertThat(repository.selectAllByIntNullable(6).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullable finds h2UuidWithoutNullable`() {
        assertThat(repository.selectAllByIntNullable(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithoutNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableNotEq finds h2UuidWithoutNullable`() {
        assertThat(repository.selectAllByIntNullableNotEq(6).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByIntNullableNotEq finds no results`() {
        assertThat(repository.selectAllByIntNullableNotEq(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableInf finds h2IntWithNullable`() {
        assertThat(repository.selectAllByIntNullableInf(7).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableInf finds no results when equals`() {
        assertThat(repository.selectAllByIntNullableInf(6).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByIntNullableInfOrEq finds h2IntWithNullable`() {
        assertThat(repository.selectAllByIntNullableInfOrEq(7).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableInfOrEq finds h2IntWithNullable when equals`() {
        assertThat(repository.selectAllByIntNullableInfOrEq(6).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableSup finds h2IntWithoutNullable`() {
        assertThat(repository.selectAllByIntNullableSup(5).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableSup finds no results when equals`() {
        assertThat(repository.selectAllByIntNullableSup(6).toIterable())
                .isEmpty()
    }

    @Test
    fun `Verify selectAllByIntNullableSupOrEq finds h2IntWithoutNullable`() {
        assertThat(repository.selectAllByIntNullableSupOrEq(5).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }

    @Test
    fun `Verify selectAllByIntNullableSupOrEq finds h2IntWithoutNullable when equals`() {
        assertThat(repository.selectAllByIntNullableSupOrEq(6).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(h2IntWithNullable)
    }
}

/**
 * @author Fred Montariol
 */
class IntRepositorySelect(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(h2Tables)

    override fun init() {
        createTables()
                .then(insertInts())
                .block()
    }

    override fun delete() {
        deleteAll()
                .block()
    }

    private fun createTables() =
            sqlClient.createTable<H2Int>()

    private fun insertInts() = sqlClient.insert(h2IntWithNullable, h2IntWithoutNullable)

    private fun deleteAll() = sqlClient.deleteAllFromTable<H2Int>()

    fun selectAllByIntNotNull(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] eq int }
            .fetchAll()

    fun selectAllByIntNotNullNotEq(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] notEq int }
            .fetchAll()

    fun selectAllByIntNotNullInf(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] inf int }
            .fetchAll()

    fun selectAllByIntNotNullInfOrEq(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] infOrEq int }
            .fetchAll()

    fun selectAllByIntNotNullSup(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] sup int }
            .fetchAll()

    fun selectAllByIntNotNullSupOrEq(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNotNull] supOrEq int }
            .fetchAll()

    fun selectAllByIntNullable(int: Int?) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] eq int }
            .fetchAll()

    fun selectAllByIntNullableNotEq(int: Int?) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] notEq int }
            .fetchAll()

    fun selectAllByIntNullableInf(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] inf int }
            .fetchAll()

    fun selectAllByIntNullableInfOrEq(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] infOrEq int }
            .fetchAll()

    fun selectAllByIntNullableSup(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] sup int }
            .fetchAll()

    fun selectAllByIntNullableSupOrEq(int: Int) = sqlClient.select<H2Int>()
            .where { it[H2Int::intNullable] supOrEq int }
            .fetchAll()
}
