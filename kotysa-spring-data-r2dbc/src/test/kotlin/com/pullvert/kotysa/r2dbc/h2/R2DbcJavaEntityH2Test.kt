/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.tables
import com.pullvert.kotysa.test.JavaUser
import com.pullvert.kotysa.test.UserDto
import com.pullvert.kotysa.test.javaBboss
import com.pullvert.kotysa.test.javaJdoe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient


class R2DbcJavaEntityH2Test : AbstractR2dbcH2Test<JavaUserH2Repository>() {
    override val context = startContext<JavaUserH2Repository>()

    override val repository = getContextRepository<JavaUserH2Repository>()

    @Test
    fun `Verify selectAll returns all users`() {
        assertThat(repository.selectAll().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(javaJdoe, javaBboss)
    }

    @Test
    fun `Verify selectFirstByFirstame finds John`() {
        assertThat(repository.selectFirstByFirstame("John").block())
                .isEqualTo(javaJdoe)
    }

    @Test
    fun `Verify selectFirstByFirstame finds no Unknown`() {
        assertThat(repository.selectFirstByFirstame("Unknown").block())
                .isNull()
    }

    @Test
    fun `Verify selectByAlias1 finds TheBoss`() {
        assertThat(repository.selectByAlias1("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaBboss)
    }

    @Test
    fun `Verify selectByAlias2 finds TheBoss`() {
        assertThat(repository.selectByAlias2("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaBboss)
    }

    @Test
    fun `Verify selectByAlias3 finds TheBoss`() {
        assertThat(repository.selectByAlias3("TheBoss").toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaBboss)
    }

    @Test
    fun `Verify selectByAlias1 with null alias finds John`() {
        assertThat(repository.selectByAlias1(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaJdoe)
    }

    @Test
    fun `Verify selectAllByAlias2 with null alias finds John`() {
        assertThat(repository.selectByAlias2(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaJdoe)
    }

    @Test
    fun `Verify selectByAlias3 with null alias finds John`() {
        assertThat(repository.selectByAlias3(null).toIterable())
                .hasSize(1)
                .containsExactlyInAnyOrder(javaJdoe)
    }

    @Test
    fun `Verify selectAllMappedToDto does the mapping`() {
        assertThat(repository.selectAllMappedToDto().toIterable())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UserDto("John Doe", null),
                        UserDto("Big Boss", "TheBoss"))
    }

    @Test
    fun `Verify deleteAll works correctly`() {
        assertThat(repository.deleteAll().block()!!)
                .isEqualTo(2)
        assertThat(repository.selectAll().toIterable())
                .isEmpty()
        // re-insert users
        repository.insert().block()
    }
}

private val tables =
        tables().h2 {
            table<JavaUser> {
                name = "java_users"
                column { it[JavaUser::getLogin].varchar().primaryKey() }
                column { it[JavaUser::getFirstname].varchar().name("fname") }
                column { it[JavaUser::getLastname].varchar().name("lname") }
                column { it[JavaUser::isAdmin].boolean() }
                column { it[JavaUser::getAlias1].varchar() }
                column { it[JavaUser::getAlias2].varchar() }
                column { it[JavaUser::getAlias3].varchar() }
            }
        }


class JavaUserH2Repository(dbClient: DatabaseClient) : Repository {

    private val sqlClient = dbClient.sqlClient(tables)

    override fun init() {
        createTable()
                .then(insert())
                .block()
    }

    override fun delete() {
        deleteAll()
                .block()
    }

    private fun createTable() = sqlClient.createTable<JavaUser>()

    fun insert() = sqlClient.insert(javaJdoe, javaBboss)

    fun deleteAll() = sqlClient.deleteAllFromTable<JavaUser>()

    fun selectAll() = sqlClient.selectAll<JavaUser>()

    fun selectFirstByFirstame(firstname: String) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getFirstname] eq firstname }
            .fetchFirst()

    fun selectByAlias1(alias: String?) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getAlias1] eq alias }
            .fetchAll()

    fun selectByAlias2(alias: String?) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getAlias2] eq alias }
            .fetchAll()

    fun selectByAlias3(alias: String?) = sqlClient.select<JavaUser>()
            .where { it[JavaUser::getAlias3] eq alias }
            .fetchAll()

    fun selectAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[JavaUser::getFirstname]} ${it[JavaUser::getLastname]}",
                        it[JavaUser::getAlias1])
            }.fetchAll()
}
