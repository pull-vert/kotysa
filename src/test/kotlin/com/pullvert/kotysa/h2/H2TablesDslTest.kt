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
                column { it[AllTypesNotNull::localDateTime].dateTime() }
                column { it[AllTypesNotNull::localDate].date() }
                column { it[AllTypesNotNull::instant].timestamp() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("string", SqlType.VARCHAR, false),
                        tuple("localDateTime", SqlType.DATE_TIME, false),
                        tuple("localDate", SqlType.DATE, false),
                        tuple("instant", SqlType.TIMESTAMP, false))
    }

    @Test
    fun `Test all supported column types for nullable properties`() {
        val tables = tables().h2 {
            table<AllTypesNullable> {
                name = "all_types_nullable"
                column { it[AllTypesNullable::id].varchar().primaryKey } // required
                column { it[AllTypesNullable::string].varchar() }
                column { it[AllTypesNullable::localDateTime].dateTime() }
                column { it[AllTypesNullable::localDate].date() }
                column { it[AllTypesNullable::instant].timestamp() }
            }
        }
        assertThat(tables.allColumns.values)
                .extracting("name", "sqlType", "isNullable")
                .containsExactly(
                        tuple("id", SqlType.VARCHAR, false),
                        tuple("string", SqlType.VARCHAR, true),
                        tuple("localDateTime", SqlType.DATE_TIME, true),
                        tuple("localDate", SqlType.DATE, true),
                        tuple("instant", SqlType.TIMESTAMP, true))
    }
}
