/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
fun <T : Any> ((T) -> Any?).toCallable(): KCallable<Any?> =
        when {
            this is KProperty1<T, *> -> this
            this is KFunction<*> -> this
            else -> throw RuntimeException("Wrong type for $this, support only KProperty1 and KFunction")
        }
