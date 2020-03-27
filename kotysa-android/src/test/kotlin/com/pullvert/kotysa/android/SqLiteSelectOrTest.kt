/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.SqLiteRole
import com.pullvert.kotysa.test.sqLiteAdmin
import com.pullvert.kotysa.test.sqLiteGod
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author Fred Montariol
 */
class SqLiteSelectOrTest : AbstractSqLiteTest<UserRepositorySelectOr>() {

    override fun getRepository(dbHelper: DbHelper, sqLiteTables: Tables) =
            UserRepositorySelectOr(dbHelper, sqLiteTables)

    @Test
    fun `Verify selectRolesByLabels finds BigBoss`() {
        assertThat(repository.selectRolesByLabels(sqLiteAdmin.label, sqLiteGod.label))
                .hasSize(2)
                .containsExactlyInAnyOrder(sqLiteAdmin, sqLiteGod)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepositorySelectOr(sqLiteOpenHelper: SQLiteOpenHelper, tables: Tables) : AbstractUserRepository(sqLiteOpenHelper, tables) {

    fun selectRolesByLabels(label1: String, label2: String) = sqlClient.select<SqLiteRole>()
            .where { it[SqLiteRole::label] eq label1 }
            .or { it[SqLiteRole::label] eq label2 }
            .fetchAll()
}
