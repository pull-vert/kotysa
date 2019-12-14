/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class TransactionalOperationR2dbc(private val operator: TransactionalOperator) : ReactorTransactionalOperation {
    override fun <T> transactional(mono: Mono<T>) = operator.transactional(mono)

    override fun <T> execute(block: (TransactionStatus) -> Flux<T>) =
        operator.execute { reactiveTransaction -> block.invoke(TransactionStatusR2dbc(reactiveTransaction)) }
}

/**
 * Create a [ReactorTransactionalOperation] from a Reactive [TransactionalOperator]
 *
 * @sample com.pullvert.kotysa.r2dbc.sample.UserRepositoryR2dbc
 * @author Fred Montariol
 */
fun TransactionalOperator.transactionalOperation(): ReactorTransactionalOperation = TransactionalOperationR2dbc(this)
