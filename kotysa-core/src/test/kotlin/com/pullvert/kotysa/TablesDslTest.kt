/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test

/**
 * @author Fred Montariol
 */
class TablesDslTest {
    @Test
    fun `Test all supported column types for not null properties`() {
        val tables = tables {
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::id].uuid().primaryKey }
                column { it[AllTypesNotNull::string].varchar() }
                column { it[AllTypesNotNull::boolean].boolean() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNull::localTim].time9() }
                column { it[AllTypesNotNull::localDateTime1].dateTime() }
                column { it[AllTypesNotNull::localDateTime2].timestamp() }
                column { it[AllTypesNotNull::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, false),
                        tuple("boolean", SqlType.BOOLEAN, false),
                        tuple("localDate", SqlType.DATE, false),
                        tuple("instant", SqlType.TIMESTAMP_WITH_TIME_ZONE, false),
                        tuple("localTim", SqlType.TIME9, false),
                        tuple("localDateTime1", SqlType.DATE_TIME, false),
                        tuple("localDateTime2", SqlType.TIMESTAMP, false),
                        tuple("uuid", SqlType.UUID, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables {
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].uuid().primaryKey }
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestampWithTimeZone() }
                column { it[AllTypesNullable::localTim].time9() }
                column { it[AllTypesNullable::localDateTime1].dateTime() }
                column { it[AllTypesNullable::localDateTime2].timestamp() }
                column { it[AllTypesNullable::uuid].uuid() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.UUID, false),
                        tuple("string", SqlType.VARCHAR, true),
                        tuple("localDate", SqlType.DATE, true),
                        tuple("instant", SqlType.TIMESTAMP_WITH_TIME_ZONE, true),
                        tuple("localTim", SqlType.TIME9, true),
                        tuple("localDateTime1", SqlType.DATE_TIME, true),
                        tuple("localDateTime2", SqlType.TIMESTAMP, true),
                        tuple("uuid", SqlType.UUID, true))
    }
}
