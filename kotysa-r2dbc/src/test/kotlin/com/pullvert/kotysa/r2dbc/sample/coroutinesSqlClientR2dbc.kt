/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.count
import com.pullvert.kotysa.r2dbc.*
import com.pullvert.kotysa.test.common.User
import com.pullvert.kotysa.test.common.sample.UserDto
import com.pullvert.kotysa.test.common.sample.bboss
import com.pullvert.kotysa.test.common.sample.jdoe
import com.pullvert.kotysa.test.common.sample.tables
import kotlinx.coroutines.FlowPreview
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
@FlowPreview
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbcCoroutines(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.coSqlClient(tables)

    suspend fun createTable() = sqlClient.createTable<User>()

    suspend fun insert() = sqlClient.insert(jdoe, bboss)

    suspend fun deleteAll() = sqlClient.deleteAllFromTable<User>()

    suspend fun deleteById(id: String) = sqlClient.deleteFromTable<User>()
            .where { it[User::id] eq id }
            .execute()

    fun selectAll() = sqlClient.selectAll<User>()

    suspend fun countAll() = sqlClient.countAll<User>()

    suspend fun countWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    suspend fun selectFirstByFirstname(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<User>()
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
            deleteAllFromTable<User>()
            insert(jdoe, bboss)

            val john = select<User>()
                    .where { it[User::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
