/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.common.*

/**
 * @author Fred Montariol
 */
abstract class AbstractUserRepository(
        sqLiteOpenHelper: SQLiteOpenHelper,
        tables: Tables
) : Repository {

    protected val sqlClient = sqLiteOpenHelper.sqlClient(tables)

    override fun init() {
        createTable()
        insertRoles()
        insertUsers()
    }

    override fun delete() {
        deleteAllFromUsers()
        deleteAllFromRoles()
    }

    fun createTable() {
        sqlClient.createTable<SqLiteRole>()
        sqlClient.createTable<SqLiteUser>()
    }

    fun insertRoles() = sqlClient.insert(sqLiteUser, sqLiteAdmin)

    fun insertUsers() = sqlClient.insert(sqLiteJdoe, sqLiteBboss)

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<SqLiteUser>()

    fun deleteAllFromRoles() = sqlClient.deleteAllFromTable<SqLiteRole>()

    fun insertJDoe() = sqlClient.insert(sqLiteJdoe)

    fun selectAll() = sqlClient.selectAll<SqLiteUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::firstname] eq firstname }
            .fetchFirstOrNull()
}