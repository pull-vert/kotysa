/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.SqLiteUser
import com.pullvert.kotysa.test.sqLiteBboss
import com.pullvert.kotysa.test.sqLiteJdoe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author Fred Montariol
 */
class SqLiteSelectBooleanTest : AbstractSqLiteTest<UserRepositoryBooleanSelect>() {

    override fun getRepository(dbHelper: DbHelper, sqLiteTables: Tables) =
            UserRepositoryBooleanSelect(dbHelper, sqLiteTables)

    @Test
    fun `Verify selectAllByIsAdminEq true finds Big Boss`() {
        assertThat(repository.selectAllByIsAdminEq(true))
                .hasSize(1)
                .containsExactly(sqLiteBboss)
    }

    @Test
    fun `Verify selectAllByIsAdminEq false finds John`() {
        assertThat(repository.selectAllByIsAdminEq(false))
                .hasSize(1)
                .containsExactly(sqLiteJdoe)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositoryBooleanSelect(sqLiteOpenHelper: SQLiteOpenHelper, tables: Tables) : AbstractUserRepository(sqLiteOpenHelper, tables) {

    fun selectAllByIsAdminEq(value: Boolean) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::isAdmin] eq value }
            .fetchAll()
}
