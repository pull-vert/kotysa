package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.common.*
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
abstract class AbstractUserRepository(dbClient: DatabaseClient) {

    protected val sqlClient = dbClient.sqlClient(h2Tables)

    fun init() {
        createTables()
                .then(deleteAllFromUsers())
                .then(deleteAllFromRole())
                .then(insertRoles())
                .then(insertUsers())
                .block()
    }

    fun createTables() =
            sqlClient.createTable<H2Role>()
                    .then(sqlClient.createTable<H2User>())

    fun insertRoles() = sqlClient.insert(h2User, h2Admin)

    fun insertUsers() = sqlClient.insert(h2Jdoe, h2Bboss)

    fun insertJDoe() = sqlClient.insert(h2Jdoe)

    fun deleteAllFromRole() = sqlClient.deleteAllFromTable<H2Role>()

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<H2User>()

    fun selectAllUsers() = sqlClient.selectAll<H2User>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            .fetchFirst()
}