/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import org.springframework.transaction.ReactiveTransaction

/**
 * @see org.springframework.transaction.TransactionExecution
 * @author Fred Montariol
 */
public class TransactionStatusR2dbc(private val reactiveTransaction: ReactiveTransaction) : TransactionStatus {

    override fun isNewTransaction(): Boolean = reactiveTransaction.isNewTransaction

    override fun setRollbackOnly() {
        reactiveTransaction.setRollbackOnly()
    }

    override fun isRollbackOnly(): Boolean = reactiveTransaction.isRollbackOnly

    override fun isCompleted(): Boolean = reactiveTransaction.isCompleted
}
