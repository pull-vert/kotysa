/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.tables
import com.pullvert.kotysa.test.common.Inherited
import com.pullvert.kotysa.test.common.Nameable
import com.pullvert.kotysa.test.common.inherited
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2

/**
 * @author Fred Montariol
 */
class R2dbcInheritanceTest {
    private val context =
            application(WebApplicationType.NONE) {
                beans {
                    bean<InheritanceRepository>()
                }
                listener<ApplicationReadyEvent> {
                    ref<InheritanceRepository>().init()
                }
                r2dbcH2()
            }.run()

    private val repository = context.getBean<InheritanceRepository>()

    @AfterAll
    fun afterAll() {
        context.close()
    }

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
        assertThat(repository.deleteById<Inherited>("id").block())
                .isEqualTo(1)
        assertThat(repository.selectAll().toIterable())
                .isEmpty()
        // re-insert
        repository.insert().block()
    }
}

private val tables =
        tables {
            table<Inherited> {
                name = "inherited"
                column { it[Inherited::getId].varchar().primaryKey }
                column { it[Inherited::name].varchar() }
                column { it[Inherited::firstname].varchar() }
            }
        }

/**
 * @author Fred Montariol
 */
class InheritanceRepository(dbClient: DatabaseClient) {

    val sqlClient = dbClient.sqlClient(tables)

    fun init() {
        createTable()
                .then(deleteAll())
                .then(insert())
                .block()
    }

    fun createTable() = sqlClient.createTable<Inherited>()

    fun insert() = sqlClient.insert(inherited)

    fun deleteAll() = sqlClient.deleteAllFromTable<Inherited>()

    fun selectAll() = sqlClient.selectAll<Inherited>()

    fun selectInheritedById(id: String) =
            sqlClient.select<Inherited>().where { it[Inherited::getId] eq id }.fetchOne()
}

inline fun <reified T : com.pullvert.kotysa.test.common.Entity<String>> InheritanceRepository.selectById(id: String) =
        sqlClient.select<T>().where { it[com.pullvert.kotysa.test.common.Entity<String>::getId] eq id }.fetchOne()

inline fun <reified T : Nameable> InheritanceRepository.selectFirstByName(name: String) =
        sqlClient.select<T>().where { it[Nameable::name] eq name }.fetchFirst()

inline fun <reified T : com.pullvert.kotysa.test.common.Entity<String>> InheritanceRepository.deleteById(id: String) =
        sqlClient.deleteFromTable<T>().where { it[com.pullvert.kotysa.test.common.Entity<String>::getId] eq id }.execute()
