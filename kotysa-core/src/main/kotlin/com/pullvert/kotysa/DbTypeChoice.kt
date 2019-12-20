/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import com.pullvert.kotysa.h2.H2TablesDsl
import com.pullvert.kotysa.postgresql.PostgresqlTablesDsl
import com.pullvert.kotysa.sqlite.SqLiteTablesDsl

/**
 * Supported Database Choice
 * @author Fred Montariol
 */
object DbTypeChoice {

    /**
     * Configure Functional Table Mapping support for H2
     * @sample com.pullvert.kotysa.sample.h2Tables
     * @see H2TablesDsl
     */
    fun h2(dsl: H2TablesDsl.() -> Unit): Tables {
        val tablesDsl = H2TablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.H2)
    }

    /**
     * Configure Functional Table Mapping support for SqLite
     * @sample com.pullvert.kotysa.sample.sqLiteTables
     * @see H2TablesDsl
     */
    fun sqlite(dsl: SqLiteTablesDsl.() -> Unit): Tables {
        val tablesDsl = SqLiteTablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.SQLITE)
    }

    /**
     * Configure Functional Table Mapping support for PostgreSQL
     * @sample com.pullvert.kotysa.sample.postgresqlTables
     * @see H2TablesDsl
     */
    fun postgresql(dsl: PostgresqlTablesDsl.() -> Unit): Tables {
        val tablesDsl = PostgresqlTablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.POSTGRESQL)
    }
}

/**
 * Choose the database's Type
 *
 * @see TablesDsl
 * @see DbTypeChoice
 * @author Fred Montariol
 */
fun tables() = DbTypeChoice
