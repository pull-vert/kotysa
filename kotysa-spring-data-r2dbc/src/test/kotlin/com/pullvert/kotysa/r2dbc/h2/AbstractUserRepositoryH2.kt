/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.*
import org.springframework.data.r2dbc.core.DatabaseClient


abstract class AbstractUserRepositoryH2(dbClient: DatabaseClient) : Repository {

    protected val sqlClient = dbClient.sqlClient(h2Tables)

    override fun init() {
        createTables()
                .then(insertRoles())
                .then(insertUsers())
                .block()
    }

    override fun delete() {
        deleteAllFromUsers()
                .then(deleteAllFromRole())
                .block()
    }

    private fun createTables() =
            sqlClient.createTable<H2Role>()
                    .then(sqlClient.createTable<H2User>())

    private fun insertRoles() = sqlClient.insert(h2User, h2Admin, h2God)

    fun insertUsers() = sqlClient.insert(h2Jdoe, h2Bboss)

    fun insertJDoe() = sqlClient.insert(h2Jdoe)

    private fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<H2User>()

    fun selectAllUsers() = sqlClient.selectAll<H2User>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirst()
}
