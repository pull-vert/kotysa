/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa

import com.pullvert.kotysa.h2.H2TablesDsl
import com.pullvert.kotysa.postgresql.PostgresqlTablesDsl
import com.pullvert.kotysa.sqlite.SqLiteTablesDsl

/**
 * Supported Database Choice
 */
public object DbTypeChoice {

    /**
     * Configure Functional Table Mapping support for H2
     * @sample com.pullvert.kotysa.sample.h2Tables
     * @see H2TablesDsl
     */
    public fun h2(dsl: H2TablesDsl.() -> Unit): Tables {
        val tablesDsl = H2TablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.H2)
    }

    /**
     * Configure Functional Table Mapping support for SqLite
     * @sample com.pullvert.kotysa.sample.sqLiteTables
     * @see H2TablesDsl
     */
    public fun sqlite(dsl: SqLiteTablesDsl.() -> Unit): Tables {
        val tablesDsl = SqLiteTablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.SQLITE)
    }

    /**
     * Configure Functional Table Mapping support for PostgreSQL
     * @sample com.pullvert.kotysa.sample.postgresqlTables
     * @see H2TablesDsl
     */
    public fun postgresql(dsl: PostgresqlTablesDsl.() -> Unit): Tables {
        val tablesDsl = PostgresqlTablesDsl(dsl)
        return tablesDsl.initialize(tablesDsl, DbType.POSTGRESQL)
    }
}

/**
 * Choose the database's Type
 *
 * @see TablesDsl
 * @see DbTypeChoice
 */
public fun tables(): DbTypeChoice = DbTypeChoice
