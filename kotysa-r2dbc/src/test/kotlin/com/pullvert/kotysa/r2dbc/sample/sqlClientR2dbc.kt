/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.count
import com.pullvert.kotysa.r2dbc.*
import com.pullvert.kotysa.samples.*
import org.springframework.data.r2dbc.core.DatabaseClient
import java.util.*

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbc(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.sqlClient(tables())

    fun createTable() = sqlClient.createTable<User>()

    fun insert() = sqlClient.insert(jdoe, bboss)

    fun deleteAll() = sqlClient.deleteAllFromTable<User>()

    fun deleteById(id: UUID) = sqlClient.deleteFromTable<User>()
            .where { it[User::id] eq id }
            .execute()

    fun selectAll() = sqlClient.selectAll<User>()

    fun countAll() = sqlClient.countAll<User>()

    fun countWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    fun selectFirstByFirstname(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()

    fun updateFirstname(newFirstname: String) = sqlClient.updateTable<User>()
            .set { it[User::firstname] = newFirstname }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<User>()
            .set { it[User::alias] = newAlias }
            .execute()


    fun simplifiedExample() {
        sqlClient.apply {
            createTable<User>()
                    .then(deleteFromTable<User>().execute()) // delete All users
                    .then(insert(jdoe, bboss))
                    .block()

            val john = select<User>()
                    .where { it[User::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
