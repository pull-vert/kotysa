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
object DatabaseChoice {

    /**
     * Configure Functional Table Mapping support for H2
     * @see H2TablesDsl
     */
    fun h2(dsl: H2TablesDsl.() -> Unit): Tables {
        val h2TablesDsl = H2TablesDsl(dsl)
        return h2TablesDsl.initialize(h2TablesDsl)
    }

    /**
     * Configure Functional Table Mapping support for SqLite
     * @see H2TablesDsl
     */
    fun sqlite(dsl: SqLiteTablesDsl.() -> Unit): Tables {
        val sqLiteTablesDsl = SqLiteTablesDsl(dsl)
        return sqLiteTablesDsl.initialize(sqLiteTablesDsl)
    }
}

/**
 * Choose the database's Type
 *
 * @see TablesDsl
 * @see DatabaseChoice
 * @author Fred Montariol
 */
fun tables() = DatabaseChoice
