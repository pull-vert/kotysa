/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

/**
 * @author Fred Montariol
 */
interface Repository {
    fun init()

    fun delete()
}