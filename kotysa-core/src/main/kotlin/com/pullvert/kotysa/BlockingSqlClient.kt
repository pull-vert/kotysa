/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * Blocking Sql Client, to be used with any blocking JDBC driver
 *
 * @author Fred Montariol
 */
interface BlockingSqlClient : SqlClient {

    fun <T : Any> insert(row: T)

    fun insert(vararg rows: Any)
}

/**
 * @author Fred Montariol
 */
class BlockingSqlClientSelect private constructor() {
    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        override fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        /**
         * This Query return one result
         *
         * @throws NoResultException if no results
         * @throws NonUniqueResultException if more than one result
         */
        fun fetchOne(): T
        /**
         * This Query return one result, or null if no results
         *
         * @throws NonUniqueResultException if more than one result
         */
        fun fetchOneOrNull(): T?
        /**
         * This Query return the first result
         *
         * @throws NoResultException if no results
         */
        fun fetchFirst(): T
        /**
         * This Query return the first result, or null if no results
         */
        fun fetchFirstOrNull(): T?
        /**
         * This Query can return several results as [List], can be empty if no results
         */
        fun fetchAll(): List<T>
    }
}

/**
 * @author Fred Montariol
 */
class BlockingSqlClientDelete private constructor() {
    interface Delete<T : Any> : SqlClientDelete.Delete<T>, Return {
        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientDelete.Where, Return

    interface Return : SqlClientDelete.Return {
        /**
         * Execute delete and return the number of deleted rows
         */
        fun execute(): Int
    }
}

/**
 * @author Fred Montariol
 */
class BlockingSqlClientUpdate private constructor() {
    interface Update<T : Any> : SqlClientUpdate.Update<T>, Return {
        override fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>

        override fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): Where
    }

    interface Where : SqlClientUpdate.Where, Return

    interface Return : SqlClientUpdate.Return {
        /**
         * Execute update and return the number of updated rows
         */
        fun execute(): Int
    }
}
