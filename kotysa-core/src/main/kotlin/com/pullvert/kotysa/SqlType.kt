/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
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
    TEXT("TEXT"),

    // numbers
    INTEGER("INTEGER"),

    // date
    TIMESTAMP("TIMESTAMP"),
    DATE("DATE"),
    DATE_TIME("DATETIME"),
    TIME("TIME"),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE"),
    TIME9("TIME(9)"), // time9 with fractional seconds precision to match with java.time.LocalTime's value

    BOOLEAN("BOOLEAN"),

    UUID("UUID")
}
