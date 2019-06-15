/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.count
import com.pullvert.kotysa.r2dbc.*
import com.pullvert.kotysa.samples.*
import kotlinx.coroutines.FlowPreview
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
@FlowPreview
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbcCoroutines(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.coSqlClient(tables())

    suspend fun createTable() = sqlClient.createTable<User>()

    suspend fun insert() = sqlClient.insert(jdoe, bboss)

    suspend fun deleteAll() = sqlClient.deleteAllFromTable<User>()

    suspend fun deleteById(id: String) = sqlClient.deleteFromTable<User>()
            .where { it[User::login] eq id }
            .execute()

    fun findAll() = sqlClient.select<User>().fetchAll()

    suspend fun countAll() = sqlClient.select { count<User>() }.fetchOne()

    suspend fun countWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    suspend fun findFirstByFirstname(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()

    suspend fun updateFirstname(newFirstname: String) = sqlClient.updateTable<User>()
            .set { it[User::firstname] = newFirstname }
            .execute()

    suspend fun updateAlias(newAlias: String?) = sqlClient.updateTable<User>()
            .set { it[User::alias] = newAlias }
            .execute()


    suspend fun simplifiedExample() {
        sqlClient.apply {
            createTable<User>()
            deleteFromTable<User>().execute() // delete All users
            insert(jdoe, bboss)

            val john = select<User>()
                    .where { it[User::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
