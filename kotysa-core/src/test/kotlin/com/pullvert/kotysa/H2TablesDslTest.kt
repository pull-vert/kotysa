/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import java.time.*
import java.util.*

/**
 * @author Fred Montariol
 */
class H2TablesDslTest {
    @Test
    fun `Test all supported column types for not null properties`() {
        val tables = tables().h2 {
            table<H2AllTypesNotNull> {
                name = "all_types"
                column { it[H2AllTypesNotNull::id].uuid().primaryKey() }
                column { it[H2AllTypesNotNull::string].varchar() }
                column { it[H2AllTypesNotNull::boolean].boolean() }
                column { it[H2AllTypesNotNull::localDate].date() }
                column { it[H2AllTypesNotNull::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNotNull::localTim].time9() }
                column { it[H2AllTypesNotNull::localDateTime1].dateTime() }
                column { it[H2AllTypesNotNull::localDateTime2].timestamp() }
                column { it[H2AllTypesNotNull::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, false),
                        tuple("boolean", SqlType.BOOLEAN, false),
                        tuple("localDate", SqlType.DATE, false),
                        tuple("offsetDateTime", SqlType.TIMESTAMP_WITH_TIME_ZONE, false),
                        tuple("localTim", SqlType.TIME9, false),
                        tuple("localDateTime1", SqlType.DATE_TIME, false),
                        tuple("localDateTime2", SqlType.TIMESTAMP, false),
                        tuple("uuid", SqlType.UUID, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables().h2 {
            table<H2AllTypesNullable> {
                name = "all_types_nullable"
                column { it[H2AllTypesNullable::id].uuid().primaryKey() }
                column { it[H2AllTypesNullable::string].varchar() }
                column { it[H2AllTypesNullable::localDate].date() }
                column { it[H2AllTypesNullable::offsetDateTime].timestampWithTimeZone() }
                column { it[H2AllTypesNullable::localTim].time9() }
                column { it[H2AllTypesNullable::localDateTime1].dateTime() }
                column { it[H2AllTypesNullable::localDateTime2].timestamp() }
                column { it[H2AllTypesNullable::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, true),
                        tuple("localDate", SqlType.DATE, true),
                        tuple("offsetDateTime", SqlType.TIMESTAMP_WITH_TIME_ZONE, true),
                        tuple("localTim", SqlType.TIME9, true),
                        tuple("localDateTime1", SqlType.DATE_TIME, true),
                        tuple("localDateTime2", SqlType.TIMESTAMP, true),
                        tuple("uuid", SqlType.UUID, true))
    }
}

/**
 * @author Fred Montariol
 */
private data class H2AllTypesNotNull(
        val id: UUID,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val offsetDateTime: OffsetDateTime,
        val localTim: LocalTime,
        val localDateTime1: LocalDateTime,
        val localDateTime2: LocalDateTime,
        val uuid: UUID
)

/**
 * @author Fred Montariol
 */
private data class H2AllTypesNullable(
        val id: UUID,
        val string: String?,
        val localDate: LocalDate?,
        val offsetDateTime: OffsetDateTime?,
        val localTim: LocalTime?,
        val localDateTime1: LocalDateTime?,
        val localDateTime2: LocalDateTime?,
        val uuid: UUID?
)
