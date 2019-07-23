/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteDatabase
import com.pullvert.kotysa.*
import kotlin.reflect.KClass

/**
 * @author Fred Montariol
 */
internal class SqlClientSqlLite(
        private val client: SQLiteDatabase,
        override val tables: Tables
) : BlockingAndroidSqlClient(), DefaultSqlClient {
    override fun <T : Any> select(resultClass: KClass<T>, dsl: (SelectDslApi.(ValueProvider) -> T)?): BlockingSqlClientSelect.Select<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any> createTable(tableClass: KClass<T>) {
        val createTableSql = createTableSql(tableClass)
        return client.execSQL(createTableSql)
    }

    override fun <T : Any> deleteFromTable(tableClass: KClass<T>): BlockingSqlClientDelete.Delete<T> =
            SqlClientDeleteSqlLite.Delete(client, tables, tableClass)

    override fun <T : Any> updateTable(tableClass: KClass<T>): BlockingSqlClientUpdate.Update<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any> insert(row: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(vararg rows: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
