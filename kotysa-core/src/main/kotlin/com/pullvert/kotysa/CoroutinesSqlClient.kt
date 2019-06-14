/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Coroutines Sql Client
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
interface CoroutinesSqlClient : SqlClient {

    override fun <T : Any> select(dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T>

    suspend fun <T : Any> insert(row: T)

    suspend fun insert(vararg rows: Any)
}


/**
 * @author Fred Montariol
 */
class CoroutinesSqlClientSelect private constructor() {

    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        suspend fun fetchOne(): T
        suspend fun fetchFirst(): T
        @FlowPreview
        fun fetchAll(): Flow<T>
    }
}

/**
 * @author Fred Montariol
 */
class CoroutinesSqlClientDelete private constructor() {

    interface Delete<T : Any> : SqlClientDelete.Delete<T>, Return {
        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientDelete.Where, Return

    interface Return : SqlClientDelete.Return {
        suspend fun execute(): Int
    }
}

/**
 * @author Fred Montariol
 */
class CoroutinesSqlClientUpdate private constructor() {

    interface Update<T : Any> : SqlClientUpdate.Update<T>, Return {
        override fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientUpdate.Where, Return

    interface Return : SqlClientUpdate.Return {
        suspend fun execute()
    }
}
