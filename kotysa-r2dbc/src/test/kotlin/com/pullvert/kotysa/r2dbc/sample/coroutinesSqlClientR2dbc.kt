/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.r2dbc.coSqlClient
import com.pullvert.kotysa.test.common.sample.*
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbcCoroutines(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.coSqlClient(h2tables)

    suspend fun simplifiedExample() {
        sqlClient.apply {
            createTable<H2User>()
            deleteAllFromTable<H2User>()
            insert(h2Jdoe, h2Bboss)

            val count = countAll<H2User>()

            val all = selectAll<H2User>()

            val johny = select { UserWithRoleDto(it[H2User::lastname], it[H2Role::label]) }
                    .innerJoinOn<H2Role> { it[H2User::roleId] }
                    .where { it[H2User::alias] eq "Johny" }
                    // null String accepted        ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
                    .fetchFirst()

            val nbUpdated = updateTable<H2User>()
                    .set { it[H2User::lastname] = "NewLastName" }
                    .innerJoinOn<H2Role> { it[H2User::roleId] }
                    .where { it[H2Role::label] eq h2User.label }
                    // null String forbidden      ^^^^^^^^^^^^
                    .execute()

            val nbDeleted = deleteFromTable<H2User>()
                    .innerJoinOn<H2Role> { it[H2User::roleId] }
                    .where { it[H2Role::label] eq h2User.label }
                    .execute()
        }
    }
}
