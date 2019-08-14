/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android.sample

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.android.*
import com.pullvert.kotysa.count
import com.pullvert.kotysa.test.common.sample.*

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositorySqLite(dbClient: SQLiteDatabase) {
    private val sqlClient = dbClient.sqlClient(sqlitetables)

    fun createTable() = sqlClient.createTable<SqLiteUser>()

    fun insert() = sqlClient.insert(sqLiteJdoe, sqLiteBboss)

    fun deleteAll() = sqlClient.deleteAllFromTable<SqLiteUser>()

    fun deleteById(id: String) = sqlClient.deleteFromTable<SqLiteUser>()
            .where { it[SqLiteUser::id] eq id }
            .execute()

    fun selectAll() = sqlClient.selectAll<SqLiteUser>()

    fun countAll() = sqlClient.countAll<SqLiteUser>()

    fun countWithAlias() = sqlClient.select { count { it[SqLiteUser::alias] } }.fetchOne()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[SqLiteUser::firstname]} ${it[SqLiteUser::lastname]}",
                        it[SqLiteUser::alias])
            }.fetchAll()

    fun selectFirstByFirstname(firstname: String) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::firstname] eq firstname }
            // null String forbidden              ^^^^^^^^^
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::alias] eq alias }
            // null String accepted           ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
            .fetchAll()

    fun updateFirstname(newFirstname: String) = sqlClient.updateTable<SqLiteUser>()
            .set { it[SqLiteUser::firstname] = newFirstname }
            .execute()

    fun updateAlias(newAlias: String?) = sqlClient.updateTable<SqLiteUser>()
            .set { it[SqLiteUser::alias] = newAlias }
            .execute()


    fun simplifiedExample() {
        sqlClient.apply {
            createTable<SqLiteUser>()
            deleteAllFromTable<SqLiteUser>()
            insert(sqLiteJdoe, sqLiteBboss)

            val john = select<SqLiteUser>()
                    .where { it[SqLiteUser::firstname] eq "John" }
                    .fetchFirst()
        }
    }
}
