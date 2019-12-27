/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import org.junit.jupiter.api.AfterAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2

/**
 * @author Fred Montariol
 */
abstract class AbstractR2dbcH2Test<T : Repository> {

    protected abstract val repository: T

    protected inline fun <reified U : Repository> startContext() =
            application(WebApplicationType.NONE) {
                beans {
                    bean<U>()
                }
                listener<ApplicationReadyEvent> {
                    ref<U>().init()
                }
                r2dbcH2()
            }.run()

    protected abstract val context: ConfigurableApplicationContext

    protected inline fun <reified U : Repository> getContextRepository() = context.getBean<U>()

    @AfterAll
    fun afterAll() {
        repository.delete()
        context.close()
    }
}