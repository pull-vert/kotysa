/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

/**
 * @author Fred Montariol
 */
// todo put in kotysa-core
interface TransactionStatus {

    fun isNewTransaction(): Boolean

    fun setRollbackOnly()

    fun isRollbackOnly(): Boolean

    fun isCompleted(): Boolean
}
