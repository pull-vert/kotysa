/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.H2LocalDate
import com.pullvert.kotysa.test.h2LocalDateWithNullable
import com.pullvert.kotysa.test.h2LocalDateWithoutNullable
import com.pullvert.kotysa.test.h2Tables
import org.springframework.data.r2dbc.core.DatabaseClient
import java.time.LocalDate

/**
 * @author Fred Montariol
 */
class R2dbcSelectLocalDateTest: AbstractR2dbcTest<LocalDateRepositorySelect>() {
    override val context = startContext<LocalDateRepositorySelect>()

    override val repository = getContextRepository<LocalDateRepositorySelect>()


}

/**
 * @author Fred Montariol
 */
class LocalDateRepositorySelect(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(h2Tables)

    override fun init() {
        createTables()
                .then(insertLocalDates())
                .block()
    }

    override fun delete() {
        deleteAll()
                .block()
    }

    private fun createTables() =
            sqlClient.createTable<H2LocalDate>()

    private fun insertLocalDates() = sqlClient.insert(h2LocalDateWithNullable, h2LocalDateWithoutNullable)

    private fun deleteAll() = sqlClient.deleteAllFromTable<H2LocalDate>()

    fun selectAllByLocalDateNotNull(localDate: LocalDate) = sqlClient.select<H2LocalDate>()
            .where { it[H2LocalDate::localDateNotNull] eq localDate }
            .fetchAll()

    fun selectAllByLocalDateNotNullNotEq(localDate: LocalDate) = sqlClient.select<H2LocalDate>()
            .where { it[H2LocalDate::localDateNotNull] notEq localDate }
            .fetchAll()

    fun selectAllByLocalDateNullable(localDate: LocalDate?) = sqlClient.select<H2LocalDate>()
            .where { it[H2LocalDate::localDateNullable] eq localDate }
            .fetchAll()

    fun selectAllByLocalDateNullableNotEq(localDate: LocalDate?) = sqlClient.select<H2LocalDate>()
            .where { it[H2LocalDate::localDateNullable] notEq localDate }
            .fetchAll()
}