/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.NoResultException
import com.pullvert.kotysa.NonUniqueResultException
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Android SDK 5.0 (API = 21) is the minimal that works
 *
 * @author Fred Montariol
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [21])
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
        assertThat(repository.selectAll())
                .hasSize(2)
                .containsExactlyInAnyOrder(sqLiteJdoe, sqLiteBboss)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() {
        assertThat(repository.selectFirstByFirstame("John"))
                .isEqualTo(sqLiteJdoe)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown"))
                .isNull()
    }

    @Test
    fun `Verify selectFirstByFirstameNotNullable finds no Unknown, throws NoResultException`() {
        assertThatThrownBy { repository.selectFirstByFirstameNotNullable("Unknown") }
                .isInstanceOf(NoResultException::class.java)
    }

    @Test
    fun `Verify selectOneNonUnique throws NonUniqueResultException`() {
        assertThatThrownBy { repository.selectOneNonUnique() }
                .isInstanceOf(NonUniqueResultException::class.java)
    }

    @Test
    fun `Verify selectByAlias finds TheBoss`() {
        assertThat(repository.selectByAlias("TheBoss").toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(sqLiteBboss)
    }

    @Test
    fun `Verify selectByAlias with null alias finds John`() {
        assertThat(repository.selectByAlias(null).toList())
                .hasSize(1)
                .containsExactlyInAnyOrder(sqLiteJdoe)
    }

    @Test
    fun `Verify selectAllMappedToDto does the mapping`() {
        assertThat(repository.selectAllMappedToDto().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify selectWithJoin works correctly`() {
        assertThat(repository.selectWithJoin())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserWithRoleDto(sqLiteJdoe.lastname, sqLiteUser.label),
                        UserWithRoleDto(sqLiteBboss.lastname, sqLiteAdmin.label)
                )
    }

    @Test
    fun `Verify deleteAllFromUser works correctly`() {
        assertThat(repository.deleteAll())
                .isEqualTo(2)
        assertThat(repository.selectAll().toList())
                .isEmpty()
        // re-insert users
        repository.insert()
    }

    @Test
    fun `Verify deleteUserById works`() {
        assertThat(repository.deleteUserById(sqLiteJdoe.id))
                .isEqualTo(1)
        assertThat(repository.selectAll())
                .hasSize(1)
        // re-insertUsers jdoe
        repository.insertJDoe()
    }

    @Test
    fun `Verify deleteUserWithJoin works`() {
        assertThat(repository.deleteUserWithJoin(sqLiteUser.label))
                .isEqualTo(1)
        assertThat(repository.selectAll())
                .hasSize(1)
                .containsOnly(sqLiteBboss)
        // re-insertUsers jdoe
        repository.insertJDoe()
    }

    @Test
    fun `Verify updateLastname works`() {
        assertThat(repository.updateLastname("Do"))
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(sqLiteJdoe.firstname))
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(sqLiteJdoe.lastname)
    }

    @Test
    fun `Verify updateWithJoin works`() {
        assertThat(repository.updateWithJoin("Do", sqLiteUser.label))
                .isEqualTo(1)
        assertThat(repository.selectFirstByFirstame(sqLiteJdoe.firstname))
                .extracting { user -> user?.lastname }
                .isEqualTo("Do")
        repository.updateLastname(sqLiteJdoe.lastname)
    }
}

/**
 * @author Fred Montariol
 */
class UserRepository(dbClient: SQLiteDatabase) {

    private val sqlClient = dbClient.sqlClient(sqLiteTables)

    fun init() {
        createTable()
        deleteAll()
        insertRoles()
        insert()
    }

    fun createTable() {
        sqlClient.createTable<SqLiteRole>()
        sqlClient.createTable<SqLiteUser>()
        sqlClient.createTable<SqLiteAllTypesNotNull>()
        sqlClient.createTable<SqLiteAllTypesNullable>()
    }

    fun insertRoles() = sqlClient.insert(sqLiteUser, sqLiteAdmin)

    fun insert() = sqlClient.insert(sqLiteJdoe, sqLiteBboss)

    fun insertJDoe() = sqlClient.insert(sqLiteJdoe)

    fun deleteAll() = sqlClient.deleteAllFromTable<SqLiteUser>()

    fun deleteUserById(id: String) = sqlClient.deleteFromTable<SqLiteUser>()
            .where { it[SqLiteUser::id] eq id }
            .execute()

    fun deleteUserWithJoin(roleLabel: String) = sqlClient.deleteFromTable<SqLiteUser>()
            .innerJoinOn<SqLiteRole> { it[SqLiteUser::roleId] }
            .where { it[SqLiteRole::label] eq roleLabel }
            .execute()

    fun selectAll() = sqlClient.selectAll<SqLiteUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::firstname] eq firstname }
            .fetchFirstOrNull()

    fun selectFirstByFirstameNotNullable(firstname: String) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::firstname] eq firstname }
            .fetchFirst()

    fun selectOneNonUnique() = sqlClient.select<SqLiteUser>()
            .fetchOne()

    fun selectByAlias(alias: String?) = sqlClient.select<SqLiteUser>()
            .where { it[SqLiteUser::alias] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[SqLiteUser::firstname]} ${it[SqLiteUser::lastname]}",
                        it[SqLiteUser::alias])
            }.fetchAll()

    fun selectWithJoin() =
            sqlClient.select { UserWithRoleDto(it[SqLiteUser::lastname], it[SqLiteRole::label]) }
                    .innerJoinOn<SqLiteRole> { it[SqLiteUser::roleId] }
                    .fetchAll()

    fun updateLastname(newLastname: String) = sqlClient.updateTable<SqLiteUser>()
            .set { it[SqLiteUser::lastname] = newLastname }
            .where { it[SqLiteUser::id] eq sqLiteJdoe.id }
            .execute()

    fun updateWithJoin(newLastname: String, roleLabel: String) = sqlClient.updateTable<SqLiteUser>()
            .set { it[SqLiteUser::lastname] = newLastname }
            .innerJoinOn<SqLiteRole> { it[SqLiteUser::roleId] }
            .where { it[SqLiteRole::label] eq roleLabel }
            .execute()
}
