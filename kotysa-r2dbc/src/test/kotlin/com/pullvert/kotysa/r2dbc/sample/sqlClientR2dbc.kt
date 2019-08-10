/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.sample

import com.pullvert.kotysa.count
import com.pullvert.kotysa.r2dbc.*
import com.pullvert.kotysa.test.common.sample.*
import org.springframework.data.r2dbc.core.DatabaseClient
import java.util.*

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositoryR2dbc(dbClient: DatabaseClient) {
    private val sqlClient = dbClient.sqlClient(h2tables)

    fun createTable() = sqlClient.createTable<H2User>()

    fun insert() = sqlClient.insert(jdoeH2, bbossH2)

    fun deleteAll() = sqlClient.deleteAllFromTable<H2User>()

    fun deleteById(id: UUID) = sqlClient.deleteFromTable<H2User>()
            .where { it[H2User::id] eq id }
            .execute()

    fun selectAll() = sqlClient.selectAll<H2User>()

    fun countAll() = sqlClient.countAll<H2User>()

    fun countWithAlias() = sqlClient.select { count { it[H2User::alias] } }.fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[H2User::firstname]} ${it[H2User::lastname]}",
                        it[H2User::alias])
            }.fetchAll()

    fun selectFirstByFirstname(firstname: String) = sqlClient.select<H2User>()
            .where { it[H2User::firstname] eq firstname }
            // null String forbidden        ^^^^^^^^^
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<H2User>()
            .where { it[H2User::alias] eq alias }
            // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()

    fun updateFirstname(newFirstname: String) = sqlClient.updateTable<H2User>()
            .set { it[H2User::firstname] = newFirstname }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<H2User>()
            .set { it[H2User::alias] = newAlias }
            .execute()


    fun simplifiedExample() {
        sqlClient.apply {
            createTable<H2User>()
                    .then(deleteAllFromTable<H2User>())
                    .then(insert(jdoeH2, bbossH2))
                    .block()

            val john = select<H2User>()
                    .where { it[H2User::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
