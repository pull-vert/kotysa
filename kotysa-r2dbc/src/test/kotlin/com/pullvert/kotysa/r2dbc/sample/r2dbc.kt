/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.r2dbc.createTable
import com.pullvert.kotysa.r2dbc.deleteFromTable
import com.pullvert.kotysa.r2dbc.select
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.tables
import org.springframework.data.r2dbc.core.DatabaseClient

private val tables =
        tables().h2 {
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::alias].varchar() }
            }
        }

private class UserRepository(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.sqlClient(tables)

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
}

private val jdoe = User("jdoe", "John", "Doe")
private val bboss = User("bboss", "Big", "Boss", "TheBoss")

private data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val alias: String? = null
)

private data class UserDto(
        val name: String,
        val alias: String?
)
