/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * All supported SQL types
 *
 * @author Fred Montariol
 */
enum class SqlType(val fullType: String) {
    // text
    VARCHAR("VARCHAR"),

    // date
    TIMESTAMP("TIMESTAMP"),
    DATE("DATE"),
    DATE_TIME("DATETIME"),
    TIME("TIME(9)"),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE"), // H2 specific

    BOOLEAN("BOOLEAN")
}
