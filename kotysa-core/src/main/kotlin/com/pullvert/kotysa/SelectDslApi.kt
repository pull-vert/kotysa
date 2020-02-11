/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
abstract class SelectDslApi protected constructor(){
    @PublishedApi
    internal abstract fun <T : Any> count(resultClass: KClass<T>, dsl: ((FieldProvider) -> ColumnField<T, *>)? = null, alias: String? = null): Long
}

/**
 * @author Fred Montariol
 */
inline fun <reified T : Any> SelectDslApi.count(noinline dsl: ((FieldProvider) -> ColumnField<T, *>)? = null) = count(T::class, dsl)
