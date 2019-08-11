/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.count
import com.pullvert.kotysa.r2dbc.*
import com.pullvert.kotysa.test.common.sample.*
import kotlinx.coroutines.FlowPreview
import org.springframework.data.r2dbc.core.DatabaseClient
import java.util.*

/**
 * @author Fred Montariol
 */
@FlowPreview
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbcCoroutines(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.coSqlClient(h2tables)

    suspend fun createTable() = sqlClient.createTable<H2User>()

    suspend fun insert() = sqlClient.insert(h2Jdoe, h2Bboss)

    suspend fun deleteAll() = sqlClient.deleteAllFromTable<H2User>()

    suspend fun deleteById(id: UUID) = sqlClient.deleteFromTable<H2User>()
            .where { it[H2User::id] eq id }
            .execute()

    fun selectAll() = sqlClient.selectAll<H2User>()

    suspend fun countAll() = sqlClient.countAll<H2User>()

    suspend fun countWithAlias() = sqlClient.select { count { it[H2User::alias] } }.fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    suspend fun selectFirstByFirstname(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()

    suspend fun updateFirstname(newFirstname: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::firstname] = newFirstname }
            .execute()

    suspend fun updateAlias(newAlias: String?) = sqlClient.updateTable<H2User>()
            .set { it[H2User::alias] = newAlias }
            .execute()


    suspend fun simplifiedExample() {
        sqlClient.apply {
            createTable<H2User>()
            deleteAllFromTable<H2User>()
            insert(h2Jdoe, h2Bboss)

            val john = select<H2User>()
                    .where { it[H2User::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
