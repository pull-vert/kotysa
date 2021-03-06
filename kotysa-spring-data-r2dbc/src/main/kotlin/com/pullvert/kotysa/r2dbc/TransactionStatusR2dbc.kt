/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.r2dbc

import org.springframework.transaction.ReactiveTransaction

/**
 * @see org.springframework.transaction.TransactionExecution
 */
public class TransactionStatusR2dbc(private val reactiveTransaction: ReactiveTransaction) : TransactionStatus {

    override fun isNewTransaction(): Boolean = reactiveTransaction.isNewTransaction

    override fun setRollbackOnly() {
        reactiveTransaction.setRollbackOnly()
    }

    override fun isRollbackOnly(): Boolean = reactiveTransaction.isRollbackOnly

    override fun isCompleted(): Boolean = reactiveTransaction.isCompleted
}
