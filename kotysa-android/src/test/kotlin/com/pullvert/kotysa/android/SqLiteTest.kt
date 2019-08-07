/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * @author Fred Montariol
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class SqLiteTest {

    private lateinit var dbHelper: DbHelper
    private lateinit var repository: UserRepository

    @Suppress("DEPRECATION")
    @Before
    fun setup() {
        dbHelper = DbHelper(RuntimeEnvironment.application)
        repository = UserRepository(dbHelper.writableDatabase)
        repository.init()
    }

    @Test
    fun `Verify selectAll returns all users`() {
        // todo implement select for SqLite then uncomment
//        assertThat(repository.selectAll())
//                .hasSize(2)
//                .containsExactlyInAnyOrder(jdoe, bboss)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepository(dbClient: SQLiteDatabase) {

    private val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTable()
        deleteAll()
        insert()
    }

    fun createTable() = sqlClient.createTable<User>()

    fun insert() = sqlClient.insert(jdoe, bboss)

    fun deleteAll() = sqlClient.deleteAllFromTable<User>()

    fun selectAll() = sqlClient.selectAll<User>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<User>()
            .where { it[User::firstname] eq firstname }
            .fetchFirst()

    fun selectByAlias(alias: String?) = sqlClient.select<User>()
            .where { it[User::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<User>()
            .set { it[User::lastname] = newLastname }
            .where { it[User::id] eq jdoe.id }
            .execute()
}
