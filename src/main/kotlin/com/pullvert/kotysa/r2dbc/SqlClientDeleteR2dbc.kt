/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc

import com.pullvert.kotysa.DefaultSqlClientDelete
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.getTable
import org.springframework.data.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientDeleteR2dbc private constructor() {
    internal class DeleteProperties<T : Any>(
            val client: DatabaseClient,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : DefaultSqlClientDelete.DeleteProperties<T>

    internal class Delete<T : Any>(
            private val client: DatabaseClient,
            override val tables: Tables,
            override val tableClass: KClass<T>
    ) : DefaultSqlClientDelete.Delete<T>, ReactorSqlClientDelete.Delete, Return<T> {

        override val deleteProperties: DeleteProperties<T>
            get() {
                return DeleteProperties(client, tables, tableClass)
            }
    }

    internal interface Return<T : Any> : DefaultSqlClientDelete.Return<T>, ReactorSqlClientDelete.Return {
        override val deleteProperties: DeleteProperties<T>

        override fun execute() = with(deleteProperties) {
            // log SQL delete
            deleteFromTableSql(tableClass)
            client.delete().from(tables.getTable(tableClass).name).fetch().rowsUpdated()
        }
    }
}
