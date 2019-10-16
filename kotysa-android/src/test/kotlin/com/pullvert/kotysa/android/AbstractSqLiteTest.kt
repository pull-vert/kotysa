package com.pullvert.kotysa.android

import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.common.sqLiteTables
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Android SDK 5.0 (API = 21) is the minimal that works
 *
 * @author Fred Montariol
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [21])
abstract class AbstractSqLiteTest<T : Repository> {

    private lateinit var dbHelper: DbHelper
    protected lateinit var repository: T

    @Suppress("DEPRECATION")
    @Before
    fun setup() {
        dbHelper = DbHelper(RuntimeEnvironment.application)
        repository = getRepository(dbHelper, sqLiteTables)
        repository.init()
    }

    internal abstract fun getRepository(dbHelper: DbHelper, sqLiteTables: Tables): T
}