/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
fun <T : Any> ((T) -> Any?).toCallable(): KCallable<Any?> =
        if (this is KProperty1<*, *>) {
            this
        } else {
            throw TODO("handle non Properties")
        }
