/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val jdoe = User("jdoe", "John", "Doe")
val bboss = User("bboss", "Big", "Boss", "TheBoss")

/**
 * @author Fred Montariol
 */
data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val alias: String? = null
) {
//	val id: Int? = null // generated auto-increment
}

/**
 * @author Fred Montariol
 */
data class UserDto(
        val name: String,
        val alias: String?
)

val allTypesNotNull = AllTypesNotNull("", LocalDateTime.now(), LocalDate.now(), Instant.now(), LocalTime.now())
val allTypesNullable = AllTypesNullable("", null, null, null, null, null)

/**
 * @author Fred Montariol
 */
data class AllTypesNotNull(
        val string: String,
        val localDateTime: LocalDateTime,
        val localDate: LocalDate,
        val instant: Instant,
        val localTim: LocalTime
)

/**
 * @author Fred Montariol
 */
data class AllTypesNullable(
        val id: String,
        val string: String?,
        val localDateTime: LocalDateTime?,
        val localDate: LocalDate?,
        val instant: Instant?,
        val localTim: LocalTime?
)
