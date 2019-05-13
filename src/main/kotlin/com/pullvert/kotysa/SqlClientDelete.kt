/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
class SqlClientDelete private constructor() {
    interface Delete : Return

    interface Return {
        fun execute(): Any
    }
}

/**
 * @author Fred Montariol
 */
class SqlClientDeleteBlocking private constructor() {
    interface Delete : SqlClientDelete.Delete, Return

    interface Return : SqlClientDelete.Return {
        override fun execute(): Int
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
internal class DefaultSqlClientDelete private constructor() {

    internal interface DeleteProperties<T : Any> {
        val tables: Tables
        val resultClass: KClass<T>
    }

    internal interface Delete<T : Any> : SqlClientDelete.Delete, Return<T> {
        val tables: Tables
        val resultClass: KClass<T>
    }

    internal interface Return<T : Any> : SqlClientSelect.Return<T> {
        val deleteProperties: DeleteProperties<T>
    }
}
