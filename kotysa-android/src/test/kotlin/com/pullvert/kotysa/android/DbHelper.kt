/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.android

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * see [Android SQLite database unit testing](https://medium.com/@elye.project/android-sqlite-database-unit-testing-is-easy-a09994701162#.s44tity8x)
 */
class DbHelper internal constructor(
        context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val TAG = DbHelper::class.java.simpleName

        private const val DATABASE_NAME = "simpledatabase.sqlite"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // no op
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(TAG, "Upgrade from version $oldVersion to $newVersion")
        Log.w(TAG, "This is version 1, no DB to update")
    }
}