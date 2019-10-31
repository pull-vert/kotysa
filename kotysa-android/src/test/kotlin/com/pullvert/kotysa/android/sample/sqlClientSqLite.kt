/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android.sample

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.android.sqlClient

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
class UserRepositorySqLite(sqLiteOpenHelper: SQLiteOpenHelper) {

    data class Role(
            val label: String,
            val id: String
    )

    data class User(
            val firstname: String,
            val lastname: String,
            val isAdmin: Boolean,
            val roleId: String,
            val alias: String? = null,
            val id: String
    )

    val tables =
            com.pullvert.kotysa.tables().sqlite {
                // choose database type
                table<Role> {
                    name = "roles"
                    column { it[Role::id].varchar().primaryKey() }
                    column { it[Role::label].varchar() }
                }
                table<User> {
                    name = "users"
                    column { it[User::id].varchar().primaryKey() }
                    column { it[User::firstname].varchar().name("fname") }
                    column { it[User::lastname].varchar().name("lname") }
                    column { it[User::isAdmin].boolean() }
                    column { it[User::roleId].varchar().foreignKey<Role>() }
                    column { it[User::alias].varchar() }
                }
            }

    val roleUser = Role("user", "ghi")
    val roleAdmin = Role("admin", "jkl")

    val userJdoe = User("John", "Doe", false, roleUser.id, id = "abc")
    val userBboss = User("Big", "Boss", true, roleAdmin.id, "TheBoss", "def")

    private class UserWithRoleDto(
            val lastname: String,
            val role: String
    )

    private val sqlClient = sqLiteOpenHelper.sqlClient(tables)

    fun simplifiedExample() = sqlClient.run {
        createTable<User>()
        deleteAllFromTable<User>()
        insert(userJdoe, userBboss)

        val count = countAll<User>()

        val all = selectAll<User>()

        val johny = select { UserWithRoleDto(it[User::lastname], it[Role::label]) }
                .innerJoin<Role>().on { it[User::roleId] }
                .where { it[User::alias] eq "Johny" }
                // null String accepted        ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
                .fetchFirst()

        val nbUpdated = updateTable<User>()
                .set { it[User::lastname] = "NewLastName" }
                .innerJoin<Role>().on { it[User::roleId] }
                .where { it[Role::label] eq roleUser.label }
                // null String forbidden      ^^^^^^^^^^^^
                .execute()

        val nbDeleted = deleteFromTable<User>()
                .innerJoin<Role>().on { it[User::roleId] }
                .where { it[Role::label] eq roleUser.label }
                .execute()
    }
}
