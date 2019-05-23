/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.h2

import com.pullvert.kotysa.AllTypesNotNull
import com.pullvert.kotysa.AllTypesNullable
import com.pullvert.kotysa.SqlType
import com.pullvert.kotysa.tables
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test

/**
 * @author Fred Montariol
 */
class H2TablesDslTest {
    @Test
    fun `Test all supported column types for not null properties`() {
        val tables = tables().h2 {
            table<AllTypesNotNull> {
                name = "all_types"
                column { it[AllTypesNotNull::string].varchar().primaryKey }
                column { it[AllTypesNotNull::localDateTime1].dateTime() }
                column { it[AllTypesNotNull::localDateTime2].timestamp() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestampWithTimeZone() }
                column { it[AllTypesNotNull::localTim].time() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("string", SqlType.VARCHAR, false),
                        tuple("localDateTime1", SqlType.DATE_TIME, false),
                        tuple("localDateTime2", SqlType.TIMESTAMP, false),
                        tuple("localDate", SqlType.DATE, false),
                        tuple("instant", SqlType.TIMESTAMP_WITH_TIME_ZONE, false),
                        tuple("localTim", SqlType.TIME, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables().h2 {
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].varchar().primaryKey } // required
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDateTime1].dateTime() }
                column { it[AllTypesNullable::localDateTime2].timestamp() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestampWithTimeZone() }
                column { it[AllTypesNullable::localTim].time() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.VARCHAR, false),
                        tuple("string", SqlType.VARCHAR, true),
                        tuple("localDateTime1", SqlType.DATE_TIME, true),
                        tuple("localDateTime2", SqlType.TIMESTAMP, true),
                        tuple("localDate", SqlType.DATE, true),
                        tuple("instant", SqlType.TIMESTAMP_WITH_TIME_ZONE, true),
                        tuple("localTim", SqlType.TIME, true))
    }
}
