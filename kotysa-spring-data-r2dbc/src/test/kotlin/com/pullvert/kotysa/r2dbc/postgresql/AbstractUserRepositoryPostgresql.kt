/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.postgresql

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.*
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
abstract class AbstractUserRepositoryPostgresql(dbClient: DatabaseClient) : Repository {

    protected val sqlClient = dbClient.sqlClient(postgresqlTables)

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
            sqlClient.createTable<PostgresqlRole>()
                    .then(sqlClient.createTable<PostgresqlUser>())

    private fun insertRoles() = sqlClient.insert(postgresqlUser, postgresqlAdmin)

    fun insertUsers() = sqlClient.insert(postgresqlJdoe, postgresqlBboss)

    fun insertJDoe() = sqlClient.insert(postgresqlJdoe)

    private fun deleteAllFromRole() = sqlClient.deleteAllFromTable<PostgresqlRole>()

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<PostgresqlUser>()

    fun selectAllUsers() = sqlClient.selectAll<PostgresqlUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<PostgresqlUser>()
            .where { it[PostgresqlUser::firstname] eq firstname }
            .fetchFirst()
}