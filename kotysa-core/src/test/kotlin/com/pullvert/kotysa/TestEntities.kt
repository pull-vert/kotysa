/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

val jdoe = User("John", "Doe", false, id = UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"))
val bboss = User("Big", "Boss", true, "TheBoss", UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"))

/**
 * @author Fred Montariol
 */
data class User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)

/**
 * @author Fred Montariol
 */
data class UserDto(
        val name: String,
        val alias: String?
)

val allTypesNotNull = AllTypesNotNull(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), "", true, LocalDate.now(), Instant.now(), LocalTime.now(), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
val allTypesNullable = AllTypesNullable(UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e"), null, null, null, null, null, null, null)

/**
 * @author Fred Montariol
 */
data class AllTypesNotNull(
        val id: UUID,
        val string: String,
        val boolean: Boolean,
        val localDate: LocalDate,
        val instant: Instant,
        val localTim: LocalTime,
        val localDateTime1: LocalDateTime,
        val localDateTime2: LocalDateTime,
        val uuid: UUID
)

/**
 * @author Fred Montariol
 */
data class AllTypesNullable(
        val id: UUID,
        val string: String?,
        val localDate: LocalDate?,
        val instant: Instant?,
        val localTim: LocalTime?,
        val localDateTime1: LocalDateTime?,
        val localDateTime2: LocalDateTime?,
        val uuid: UUID?
)

// test inheritance
val inherited = Inherited("id", "name", "firstname")

/**
 * @author Fred Montariol
 */
interface Nameable {
    val name: String
}

/**
 * @author Fred Montariol
 */
interface DummyIntermediary : Nameable

/**
 * @author Fred Montariol
 */
open class Inherited(
        private val id: String,
        override val name: String,
        val firstname: String?
) : DummyIntermediary, Entity<String> {

    override fun getId() = id

    // try to bring ambiguity for reflection on name val
    protected fun name() = ""

    internal fun getName() = ""
    @Suppress("UNUSED_PARAMETER")
    fun getName(dummyParam: Boolean) = ""

    // not a data class so needs hashCode & equals functions

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Inherited

        if (name != other.name) return false
        if (firstname != other.firstname) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (firstname?.hashCode() ?: 0)
        return result
    }
}

val jdoeJava: JavaUser
    get() {
        val javaUser = JavaUser()
        javaUser.login = "jdoe"
        javaUser.firstname = "John"
        javaUser.lastname = "Doe"
        javaUser.isAdmin = false
        return javaUser
    }

val bbossJava: JavaUser
    get() {
        val javaUser = JavaUser()
        javaUser.login = "bboss"
        javaUser.firstname = "Big"
        javaUser.lastname = "Boss"
        javaUser.isAdmin = true
        javaUser.alias1 = "TheBoss"
        javaUser.alias2 = "TheBoss"
        javaUser.alias3 = "TheBoss"
        return javaUser
    }

val tables =
        tables {
            table<User> {
                name = "users"
                column { it[User::id].uuid().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
        }
