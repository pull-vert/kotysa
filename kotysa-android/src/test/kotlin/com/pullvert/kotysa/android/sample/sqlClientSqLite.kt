/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android.sample

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.android.sqlClient
import com.pullvert.kotysa.test.common.sample.*

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositorySqLite(dbClient: SQLiteDatabase) {
    private val sqlClient = dbClient.sqlClient(sqlitetables)

    fun simplifiedExample() {
        sqlClient.apply {
            createTable<SqLiteUser>()
            deleteAllFromTable<SqLiteUser>()
            insert(sqLiteJdoe, sqLiteBboss)

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
