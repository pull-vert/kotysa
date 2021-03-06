/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.*

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

    private fun createTable() {
        sqlClient.createTable<SqLiteRole>()
        sqlClient.createTable<SqLiteUser>()
    }

    private fun insertRoles() = sqlClient.insert(sqLiteUser, sqLiteAdmin, sqLiteGod)

    fun insertUsers() = sqlClient.insert(sqLiteJdoe, sqLiteBboss)

    fun deleteAllFromUsers() = sqlClient.deleteAllFromTable<SqLiteUser>()

    private fun deleteAllFromRoles() = sqlClient.deleteAllFromTable<SqLiteRole>()

    fun insertJDoe() = sqlClient.insert(sqLiteJdoe)

    fun selectAll() = sqlClient.selectAll<SqLiteUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::firstname] eq firstname }
            .fetchFirstOrNull()
}
