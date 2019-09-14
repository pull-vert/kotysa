/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * Reactive (using Reactor Mono and Flux) Sql Client, to be used with R2dbc
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
abstract class ReactorSqlClient {

    abstract fun <T : Any> insert(row: T): Mono<Void>

    abstract fun insert(vararg rows: Any): Mono<Void>

    inline fun <reified T : Any> select(noinline dsl: SelectDslApi.(ValueProvider) -> T) = select(T::class, dsl)

    inline fun <reified T : Any> select() = select(T::class, null)

    inline fun <reified T : Any> selectAll() = select(T::class, null).fetchAll()

    inline fun <reified T : Any> countAll() = select(Long::class) { count<T>() }.fetchOne()

    @PublishedApi
    internal abstract fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): ReactorSqlClientSelect.Select<T>

    inline fun <reified T : Any> createTable() = createTable(T::class)

    @PublishedApi
    internal abstract fun <T : Any> createTable(tableClass: KClass<T>): Mono<Void>

    inline fun <reified T : Any> deleteFromTable() = deleteFromTable(T::class)

    inline fun <reified T : Any> deleteAllFromTable() = deleteFromTable(T::class).execute()

    @PublishedApi
    internal abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): ReactorSqlClientDeleteOrUpdate.DeleteOrUpdate<T>

    inline fun <reified T : Any> updateTable() = updateTable(T::class)

    @PublishedApi
    internal abstract fun <T : Any> updateTable(tableClass: KClass<T>): ReactorSqlClientDeleteOrUpdate.Update<T>
}


/**
 * @author Fred Montariol
 */
class ReactorSqlClientSelect private constructor() {

    abstract class Select<T : Any> : Whereable<T>, Return<T> {
        inline fun <reified T : Any> innerJoinOn(alias: String? = null, noinline dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOn(T::class, alias, JoinType.INNER, dsl)

        @PublishedApi
        internal abstract fun <U : Any> joinOn(joinClass: KClass<U>, alias: String?, type: JoinType,
                                               dsl: (FieldProvider) -> ColumnField<*, *>): Join<T>
    }

    interface Whereable<T : Any> {
        fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Join<T : Any> : Whereable<T>, Return<T>

    interface Where<T : Any> : Return<T>

    interface Return<T : Any> {
        /**
         * This Query return one result as [Mono], or an empty [Mono] if no result
         *
         * @throws NonUniqueResultException if more than one result
         */
        fun fetchOne(): Mono<T>

        /**
         * This Query return one result as [Mono], or an empty [Mono] if no result
         */
        fun fetchFirst(): Mono<T>

        /**
         * This Query can return several results as [Flux], or an empty [Flux] if no result
         */
        fun fetchAll(): Flux<T>
    }
}

/**
 * @author Fred Montariol
 */
class ReactorSqlClientDeleteOrUpdate private constructor() {

    abstract class DeleteOrUpdate<T : Any> : Return {
        inline fun <reified T : Any> innerJoinOn(alias: String? = null, noinline dsl: (FieldProvider) -> ColumnField<*, *>) =
                joinOn(T::class, alias, JoinType.INNER, dsl)

        @PublishedApi
        internal abstract fun <U : Any> joinOn(
                joinClass: KClass<U>, alias: String?, type: JoinType, dsl: (FieldProvider) -> ColumnField<*, *>): Join

        abstract fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): TypedWhere<T>
    }

    abstract class Update<T : Any> : DeleteOrUpdate<T>() {
        abstract fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>
    }

    interface Join : Return {
        fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    interface Where : Return // and function will not be typed

    interface TypedWhere<T : Any> : Return // and function will be typed

    interface Return {
        /**
         * Execute delete or update and return the number of updated or deleted rows
         */
        fun execute(): Mono<Int>
    }
}
