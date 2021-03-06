/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc.postgresql

import com.pullvert.kotysa.r2dbc.Repository
import org.junit.jupiter.api.AfterAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcPostgresql
import org.testcontainers.containers.PostgreSQLContainer

class KPostgreSQLContainer : PostgreSQLContainer<KPostgreSQLContainer>()


abstract class AbstractR2dbcPostgresqlTest<T : Repository> {

    protected abstract val repository: T

    protected inline fun <reified U : Repository> startContext(): ConfigurableApplicationContext {
        // PostgreSQL testcontainers must be started first to get random Docker mapped port
        val postgresqlContainer = KPostgreSQLContainer()
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("")
        postgresqlContainer.start()

        return application(WebApplicationType.NONE) {
            beans {
                bean { postgresqlContainer }
                bean<U>()
            }
            listener<ApplicationReadyEvent> {
                ref<U>().init()
            }
            r2dbcPostgresql {
                port = postgresqlContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
            }
        }.run()
    }

    protected abstract val context: ConfigurableApplicationContext

    protected inline fun <reified U : Repository> getContextRepository() = context.getBean<U>()

    @AfterAll
    fun afterAll() = context.run {
        repository.delete()
        getBean<PostgreSQLContainer<*>>().stop()
        close()
    }
}
