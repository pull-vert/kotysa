/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.test

/**
 * @author Fred Montariol
 */
data class UserDto(
        val name: String,
        val alias: String?
)

/**
 * @author Fred Montariol
 */
data class UserWithRoleDto(
        val lastname: String,
        val role: String
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

val javaJdoe: JavaUser
    get() {
        val javaUser = JavaUser()
        javaUser.login = "jdoe"
        javaUser.firstname = "John"
        javaUser.lastname = "Doe"
        javaUser.isAdmin = false
        return javaUser
    }

val javaBboss: JavaUser
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
