/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.samples

import com.pullvert.kotysa.SqlClientBlocking
import com.pullvert.kotysa.createTable
import com.pullvert.kotysa.deleteFromTable
import com.pullvert.kotysa.select

@Suppress("UNUSED_VARIABLE")
interface UserRepositoryBlocking {
    val sqlClient: SqlClientBlocking

    fun createTable() = sqlClient.createTable<User>()

    fun insert() = sqlClient.insert(jdoe, bboss)

    fun deleteAll() = sqlClient.deleteFromTable<User>().execute()

    fun findAll() = sqlClient.select<User>().fetchAll()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    fun findFirstByFirstname(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun findAllByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()


    fun simplifiedExample() {
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