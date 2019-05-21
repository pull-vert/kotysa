/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author Fred Montariol
 */
class TablesDslTest {
    @Test
    fun `Test all supported column types for not null properties`() {
        val tables = tables {
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::varchar].varchar().primaryKey }
                column { it[AllTypesNotNull::dateTime].dateTime() }
                column { it[AllTypesNotNull::date].date() }
//                column { it[AllTypesNotNull::timestamp].timestamp() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("varchar", SqlType.VARCHAR, false),
                        tuple("dateTime", SqlType.DATE_TIME, false),
                        tuple("date", SqlType.DATE, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables {
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::varchar].varchar() }
                column { it[AllTypesNullable::dateTime].dateTime() }
                column { it[AllTypesNullable::date].date() }
//                column { it[AllTypesNotNull::timestamp].timestamp() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("varchar", SqlType.VARCHAR, true),
                        tuple("dateTime", SqlType.DATE_TIME, true),
                        tuple("date", SqlType.DATE, true))
    }
}

/**
 * @author Fred Montariol
 */
data class AllTypesNotNull(
        val varchar: String,
        val dateTime: LocalDateTime,
        val date: LocalDate,
        val timestamp: Instant
)

/**
 * @author Fred Montariol
 */
data class AllTypesNullable(
        val varchar: String?,
        val dateTime: LocalDateTime?,
        val date: LocalDate?,
        val timestamp: Instant?
)
