/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import com.pullvert.kotysa.h2.H2TablesDsl
import com.pullvert.kotysa.sqlite.SqLiteTablesDsl

/**
 * Supported Database Choice
 * @author Fred Montariol
 */
object DbTypeChoice {

    /**
     * Configure Functional Table Mapping support for H2
     * @sample com.pullvert.kotysa.sample.h2tables
     * @see H2TablesDsl
     */
    fun h2(dsl: H2TablesDsl.() -> Unit): Tables {
        val h2TablesDsl = H2TablesDsl(dsl)
        return h2TablesDsl.initialize(h2TablesDsl, DbType.H2)
    }

    /**
     * Configure Functional Table Mapping support for SqLite
     * @sample com.pullvert.kotysa.sample.h2tables
     * @see H2TablesDsl
     */
    fun sqlite(dsl: SqLiteTablesDsl.() -> Unit): Tables {
        val sqLiteTablesDsl = SqLiteTablesDsl(dsl)
        return sqLiteTablesDsl.initialize(sqLiteTablesDsl, DbType.SQLITE)
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
