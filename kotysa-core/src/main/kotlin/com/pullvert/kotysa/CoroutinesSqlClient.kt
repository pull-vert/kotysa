/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * Coroutines Sql Client
 *
 * @author Fred Montariol
 */
abstract class CoroutinesSqlClient {

    abstract suspend fun <T : Any> insert(row: T)

    abstract suspend fun insert(vararg rows: Any)

    inline fun <reified T : Any> select(noinline dsl: SelectDslApi.(ValueProvider) -> T) = selectInternal(T::class, dsl)

    inline fun <reified T : Any> select() = selectInternal(T::class, null)

    @ExperimentalCoroutinesApi
    inline fun <reified T : Any> selectAll() = selectInternal(T::class, null).fetchAll()

    suspend inline fun <reified T : Any> countAll() = selectInternal(Long::class) { count<T>() }.fetchOne()

    @PublishedApi
    internal fun <T : Any> selectInternal(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?) =
            select(resultClass, dsl)

    protected abstract fun <T : Any> select(
            resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T>

    suspend inline fun <reified T : Any> createTable() = createTableInternal(T::class)

    @PublishedApi
    internal suspend fun <T : Any> createTableInternal(tableClass: KClass<T>) = createTable(tableClass)

    protected abstract suspend fun <T : Any> createTable(tableClass: KClass<T>)

    inline fun <reified T : Any> deleteFromTable() = deleteFromTableInternal(T::class)

    suspend inline fun <reified T : Any> deleteAllFromTable() = deleteFromTableInternal(T::class).execute()

    @PublishedApi
    internal fun <T : Any> deleteFromTableInternal(tableClass: KClass<T>) =
            deleteFromTable(tableClass)

    protected abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T>

    inline fun <reified T : Any> updateTable() = updateTableInternal(T::class)

    @PublishedApi
    internal fun <T : Any> updateTableInternal(tableClass: KClass<T>) =
            updateTable(tableClass)

    protected abstract fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.Update<T>
}


/**
 * @author Fred Montariol
 */
class CoroutinesSqlClientSelect private constructor() {

    abstract class Select<T : Any> : Whereable<T>, Return<T> {
        inline fun <reified T : Any> innerJoinOn(alias: String? = null, noinline dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOnInternal(T::class, alias, JoinType.INNER, dsl)

        @PublishedApi
        internal fun <U : Any> joinOnInternal(joinClass: KClass<U>, alias: String?, type: JoinType,
                                              dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOn(joinClass, alias, type, dsl)

        protected abstract fun <U : Any> joinOn(
                joinClass: KClass<U>, alias: String?, type: JoinType, dsl: (FieldProvider) -> ColumnField<*, *>): Join<T>
    }

    interface Whereable<T : Any> {
        fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Join<T : Any> : Whereable<T>, Return<T>

    interface Where<T : Any> : Return<T>

    interface Return<T : Any> {
        /**
         * This Query return one result
         *
         * @throws NoResultException if no results
         * @throws NonUniqueResultException if more than one result
         */
        suspend fun fetchOne(): T

        /**
         * This Query return one result, or null if no results
         *
         * @throws NonUniqueResultException if more than one result
         */
        suspend fun fetchOneOrNull(): T?

        /**
         * This Query return the first result
         *
         * @throws NoResultException if no results
         */
        suspend fun fetchFirst(): T

        /**
         * This Query return the first result, or null if no results
         */
        suspend fun fetchFirstOrNull(): T?

        /**
         * This Query can return several results as [Flow], can be empty if no results
         */
        @ExperimentalCoroutinesApi
        fun fetchAll(): Flow<T>
    }
}

/**
 * @author Fred Montariol
 */
class CoroutinesSqlClientDeleteOrUpdate private constructor() {

    abstract class DeleteOrUpdate<T : Any> : Return {
        inline fun <reified T : Any> innerJoinOn(alias: String? = null, noinline dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOnInternal(T::class, alias, JoinType.INNER, dsl)

        @PublishedApi
        internal fun <U : Any> joinOnInternal(joinClass: KClass<U>, alias: String?, type: JoinType,
                                              dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOn(joinClass, alias, type, dsl)

        protected abstract fun <U : Any> joinOn(
                joinClass: KClass<U>, alias: String?, type: JoinType, dsl: (FieldProvider) -> ColumnField<*, *>): Join<T>

        abstract fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): TypedWhere<T>
    }

    abstract class Update<T : Any> : DeleteOrUpdate<T>() {
        abstract fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>
    }

    interface Join<T : Any> : Return {
        fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    interface Where : Return // and function will not be typed

    interface TypedWhere<T : Any> : Return // and function will be typed

    interface Return {
        /**
         * Execute delete or update and return the number of updated or deleted rows
         */
        suspend fun execute(): Int
    }
}
