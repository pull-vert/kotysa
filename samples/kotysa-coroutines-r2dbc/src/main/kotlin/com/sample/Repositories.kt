package com.sample

import com.pullvert.kotysa.CoroutinesSqlClient
import java.util.*

private val role_user_uuid = UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f")
private val role_admin_uuid = UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e")

class UserRepository(private val client: CoroutinesSqlClient) {

    suspend fun count() = client.countAll<User>()

    fun findAll() = client.selectAll<User>()

    suspend fun findOne(id: Int) =
            client.select<User>()
                    .where { it[User::id] eq id }
                    .fetchOne()

    fun selectWithJoin() =
            client.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}", it[User::alias], it[Role::label])
            }
                    .innerJoin<Role>().on { it[User::roleId] }
                    .fetchAll()

    suspend fun deleteAll() = client.deleteAllFromTable<User>()

    suspend fun save(user: User) = client.insert(user)

    suspend fun init() {
        client.createTable<User>()
        deleteAll()
        save(User("John", "Doe", false, role_user_uuid, id = 123))
        save(User("Big", "Boss", true, role_admin_uuid, "TheBoss"))
    }
}

class RoleRepository(private val client: CoroutinesSqlClient) {
    suspend fun deleteAll() = client.deleteAllFromTable<Role>()

    suspend fun save(role: Role) = client.insert(role)

    suspend fun init() {
        client.createTable<Role>()
        deleteAll()
        save(Role("user", role_user_uuid))
        save(Role("admin", role_admin_uuid))
        save(Role("god"))
    }
}
