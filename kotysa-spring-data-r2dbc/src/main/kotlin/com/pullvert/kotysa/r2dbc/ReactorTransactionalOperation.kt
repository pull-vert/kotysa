/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @see org.springframework.transaction.reactive.TransactionalOperator
 * @author Fred Montariol
 */
interface ReactorTransactionalOperation {

    fun <T> transactional(flux: Flux<T>) = execute { _ -> flux }

    fun <T> transactional(mono: Mono<T>): Mono<T>

    fun <T> execute(block: (TransactionStatus) -> Flux<T>): Flux<T>
}
