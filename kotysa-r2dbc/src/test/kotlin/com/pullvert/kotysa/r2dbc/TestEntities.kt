/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

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
        javaUser.alias = "TheBoss"
        return javaUser
    }
