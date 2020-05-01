/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * Coroutines Sql Client
 */
public abstract class CoroutinesSqlClient {

    public abstract suspend fun <T : Any> insert(row: T)

    public abstract suspend fun insert(vararg rows: Any)

    public inline fun <reified T : Any> select(
            noinline dsl: SelectDslApi.(ValueProvider) -> T
    ): CoroutinesSqlClientSelect.Select<T> = selectInternal(T::class, dsl)

    public inline fun <reified T : Any> select(): CoroutinesSqlClientSelect.Select<T> = selectInternal(T::class, null)

    public inline fun <reified T : Any> selectAll(): Flow<T> = selectInternal(T::class, null).fetchAll()

    public suspend inline fun <reified T : Any> countAll(): Long = selectInternal(Long::class) { count<T>() }.fetchOne()

    @PublishedApi
    internal fun <T : Any> selectInternal(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?) =
            select(resultClass, dsl)

    protected abstract fun <T : Any> select(
            resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): CoroutinesSqlClientSelect.Select<T>

    public suspend inline fun <reified T : Any> createTable(): Unit = createTableInternal(T::class)

    @PublishedApi
    internal suspend fun <T : Any> createTableInternal(tableClass: KClass<T>) = createTable(tableClass)

    protected abstract suspend fun <T : Any> createTable(tableClass: KClass<T>)

    public inline fun <reified T : Any> deleteFromTable(): CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T> =
            deleteFromTableInternal(T::class)

    public suspend inline fun <reified T : Any> deleteAllFromTable(): Int = deleteFromTableInternal(T::class).execute()

    @PublishedApi
    internal fun <T : Any> deleteFromTableInternal(tableClass: KClass<T>) =
            deleteFromTable(tableClass)

    protected abstract fun <T : Any> deleteFromTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.DeleteOrUpdate<T>

    public inline fun <reified T : Any> updateTable(): CoroutinesSqlClientDeleteOrUpdate.Update<T> = updateTableInternal(T::class)

    @PublishedApi
    internal fun <T : Any> updateTableInternal(tableClass: KClass<T>) = updateTable(tableClass)

    protected abstract fun <T : Any> updateTable(tableClass: KClass<T>): CoroutinesSqlClientDeleteOrUpdate.Update<T>
}



public class CoroutinesSqlClientSelect private constructor() {

    public abstract class Select<T : Any> : Whereable<T>, Return<T> {

        public inline fun <reified U : Any> innerJoin(alias: String? = null): Joinable<T> =
                joinInternal(U::class, alias, JoinType.INNER)

        @PublishedApi
        internal fun <U : Any> joinInternal(joinClass: KClass<U>, alias: String?, type: JoinType) =
                join(joinClass, alias, type)

        protected abstract fun <U : Any> join(joinClass: KClass<U>, alias: String?, type: JoinType): Joinable<T>
    }

    public interface Joinable<T : Any> {
        public fun on(dsl: (FieldProvider) -> ColumnField<*, *>): Join<T>
    }

    public interface Join<T : Any> : Whereable<T>, Return<T>

    public interface Whereable<T : Any> {
        public fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    public interface Where<T : Any> : Return<T> {
        public fun and(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
        public fun or(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    public interface Return<T : Any> {
        /**
         * This Query return one result
         *
         * @throws NoResultException if no results
         * @throws NonUniqueResultException if more than one result
         */
        public suspend fun fetchOne(): T

        /**
         * This Query return one result, or null if no results
         *
         * @throws NonUniqueResultException if more than one result
         */
        public suspend fun fetchOneOrNull(): T?

        /**
         * This Query return the first result
         *
         * @throws NoResultException if no results
         */
        public suspend fun fetchFirst(): T

        /**
         * This Query return the first result, or null if no results
         */
        public suspend fun fetchFirstOrNull(): T?

        /**
         * This Query can return several results as [Flow], can be empty if no results
         */
        public fun fetchAll(): Flow<T>
    }
}


public class CoroutinesSqlClientDeleteOrUpdate private constructor() {

    public abstract class DeleteOrUpdate<T : Any> : Return {

        public inline fun <reified U : Any> innerJoin(alias: String? = null): Joinable =
                joinInternal(U::class, alias, JoinType.INNER)

        @PublishedApi
        internal fun <U : Any> joinInternal(joinClass: KClass<U>, alias: String?, type: JoinType) =
                join(joinClass, alias, type)

        protected abstract fun <U : Any> join(joinClass: KClass<U>, alias: String?, type: JoinType): Joinable

        public abstract fun where(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): TypedWhere<T>
    }

    public abstract class Update<T : Any> : DeleteOrUpdate<T>() {
        public abstract fun set(dsl: (FieldSetter<T>) -> Unit): Update<T>
    }

    public interface Joinable {
        public fun on(dsl: (FieldProvider) -> ColumnField<*, *>): Join
    }

    public interface Join : Return {
        public fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    public interface Where : Return {
        public fun and(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where
        public fun or(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where
    }

    public interface TypedWhere<T : Any> : Return {
        public fun and(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): TypedWhere<T>
        public fun or(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause): TypedWhere<T>
    }

    public interface Return {
        /**
         * Execute delete or update and return the number of updated or deleted rows
         */
        public suspend fun execute(): Int
    }
}
