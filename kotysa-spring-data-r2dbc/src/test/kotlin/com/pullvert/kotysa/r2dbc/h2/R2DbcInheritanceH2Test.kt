/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.tables
import com.pullvert.kotysa.test.Entity
import com.pullvert.kotysa.test.Inherited
import com.pullvert.kotysa.test.Nameable
import com.pullvert.kotysa.test.inherited
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * @author Fred Montariol
 */
class R2DbcInheritanceH2Test : AbstractR2dbcH2Test<InheritanceRepository>() {
    override val context = startContext<InheritanceRepository>()

    override val repository = getContextRepository<InheritanceRepository>()

    @Test
    fun `Verify extension function selectById finds inherited`() {
        assertThat(repository.selectById<Inherited>("id").block())
                .isEqualTo(inherited)
    }

    @Test
    fun `Verify selectInheritedById finds inherited`() {
        assertThat(repository.selectInheritedById("id").block())
                .isEqualTo(inherited)
    }

    @Test
    fun `Verify selectFirstByName finds inherited`() {
        assertThat(repository.selectFirstByName<Inherited>("name").block())
                .isEqualTo(inherited)
    }

    @Test
    fun `Verify deleteById deletes inherited`() {
        assertThat(repository.deleteById<Inherited>("id").block()!!)
                .isEqualTo(1)
        assertThat(repository.selectAll().toIterable())
                .isEmpty()
        // re-insert
        repository.insert().block()
    }
}

private val tables =
        tables().h2 {
            table<Inherited> {
                name = "inherited"
                column { it[Inherited::getId].varchar().primaryKey() }
                column { it[Inherited::name].varchar() }
                column { it[Inherited::firstname].varchar() }
            }
        }

/**
 * @author Fred Montariol
 */
class InheritanceRepository(dbClient: DatabaseClient) : Repository {

    val sqlClient = dbClient.sqlClient(tables)

    override fun init() {
        createTable()
                .then(insert())
                .block()
    }

    override fun delete() {
        deleteAll()
                .block()
    }

    private fun createTable() = sqlClient.createTable<Inherited>()

    fun insert() = sqlClient.insert(inherited)

    private fun deleteAll() = sqlClient.deleteAllFromTable<Inherited>()

    fun selectAll() = sqlClient.selectAll<Inherited>()

    fun selectInheritedById(id: String) =
            sqlClient.select<Inherited>().where { it[Inherited::getId] eq id }.fetchOne()
}

inline fun <reified T : Entity<String>> InheritanceRepository.selectById(id: String) =
        sqlClient.select<T>().where { it[Entity<String>::getId] eq id }.fetchOne()

inline fun <reified T : Nameable> InheritanceRepository.selectFirstByName(name: String) =
        sqlClient.select<T>().where { it[Nameable::name] eq name }.fetchFirst()

inline fun <reified T : Entity<String>> InheritanceRepository.deleteById(id: String) =
        sqlClient.deleteFromTable<T>().where { it[Entity<String>::getId] eq id }.execute()
