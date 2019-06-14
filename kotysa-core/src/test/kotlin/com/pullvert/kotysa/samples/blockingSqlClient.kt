/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.samples

import com.pullvert.kotysa.BlockingSqlClient
import com.pullvert.kotysa.count

/**
 * @author Fred Montariol
 */
@Suppress("UNUSED_VARIABLE")
interface UserRepositoryBlocking {
    val sqlClient: BlockingSqlClient

    fun insert() = sqlClient.insert(jdoe, bboss)

    fun countAll() = sqlClient.select { count<User>() }.fetchOne()

    fun countWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

    fun findAllMappedToDto() =
            sqlClient.select {
                UserDto("${it[User::firstname]} ${it[User::lastname]}",
                        it[User::alias])
            }.fetchAll()
}
