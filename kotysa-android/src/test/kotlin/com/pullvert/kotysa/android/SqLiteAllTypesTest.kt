/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package com.pullvert.kotysa.android

import android.database.sqlite.SQLiteOpenHelper
import com.pullvert.kotysa.Tables
import com.pullvert.kotysa.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

class SqLiteAllTypesTest : AbstractSqLiteTest<AllTypesRepository>() {

    override fun getRepository(dbHelper: DbHelper, sqLiteTables: Tables) =
            AllTypesRepository(dbHelper, sqLiteTables)

    @Test
    fun `Verify selectAllAllTypesNotNull returns all AllTypesNotNull`() {
        assertThat(repository.selectAllAllTypesNotNull())
                .hasSize(1)
                .containsExactly(sqLiteAllTypesNotNull)
    }

    @Test
    fun `Verify selectAllAllTypesNullableDefaultValue returns all AllTypesNullableDefaultValue`() {
        assertThat(repository.selectAllAllTypesNullableDefaultValue())
                .hasSize(1)
                .containsExactly(SqLiteAllTypesNullableDefaultValue(
                        sqLiteAllTypesNullableDefaultValue.id,
                        "default",
                        LocalDate.MAX,
                        OffsetDateTime.MAX,
                        LocalDateTime.MAX,
                        LocalTime.MAX,
                        42
                ))
    }

    @Test
    fun `Verify selectAllAllTypesNullable returns all AllTypesNullable`() {
        assertThat(repository.selectAllAllTypesNullable())
                .hasSize(1)
                .containsExactly(sqLiteAllTypesNullable)
    }

    @Test
    fun `Verify updateAll works`() {
        val newLocalDate = LocalDate.now()
        val newOffsetDateTime = OffsetDateTime.now()
        val newLocalTime = LocalTime.now()
        val newLocalDateTime = LocalDateTime.now()
        val newInt = 2
        repository.updateAllTypesNotNull("new", false, newLocalDate, newOffsetDateTime, newLocalTime,
                newLocalDateTime, newInt)
        assertThat(repository.selectAllAllTypesNotNull())
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        SqLiteAllTypesNotNull(sqLiteAllTypesNotNull.id, "new", false, newLocalDate, newOffsetDateTime,
                                newLocalDateTime, newLocalTime, newInt))
        repository.updateAllTypesNotNull(sqLiteAllTypesNotNull.string, sqLiteAllTypesNotNull.boolean,
                sqLiteAllTypesNotNull.localDate, sqLiteAllTypesNotNull.offsetDateTime, sqLiteAllTypesNotNull.localTime,
                sqLiteAllTypesNotNull.localDateTime, sqLiteAllTypesNotNull.int)
    }
}

class AllTypesRepository(sqLiteOpenHelper: SQLiteOpenHelper, tables: Tables) : Repository {

    private val sqlClient = sqLiteOpenHelper.sqlClient(tables)

    override fun init() {
        createTables()
        insertAllTypes()
    }

    override fun delete() {
        deleteAll()
    }

    private fun createTables() {
        sqlClient.createTable<SqLiteAllTypesNotNull>()
        sqlClient.createTable<SqLiteAllTypesNullable>()
        sqlClient.createTable<SqLiteAllTypesNullableDefaultValue>()
    }

    private fun insertAllTypes() = sqlClient.insert(sqLiteAllTypesNotNull, sqLiteAllTypesNullable,
            sqLiteAllTypesNullableDefaultValue)

    private fun deleteAll() {
        sqlClient.deleteAllFromTable<SqLiteAllTypesNotNull>()
        sqlClient.deleteAllFromTable<SqLiteAllTypesNullable>()
        sqlClient.deleteAllFromTable<SqLiteAllTypesNullableDefaultValue>()
    }

    fun selectAllAllTypesNotNull() = sqlClient.selectAll<SqLiteAllTypesNotNull>()

    fun selectAllAllTypesNullable() = sqlClient.selectAll<SqLiteAllTypesNullable>()

    fun selectAllAllTypesNullableDefaultValue() = sqlClient.selectAll<SqLiteAllTypesNullableDefaultValue>()

    fun updateAllTypesNotNull(newString: String, newBoolean: Boolean, newLocalDate: LocalDate,
                              newOffsetDateTime: OffsetDateTime, newLocalTime: LocalTime, newLocalDateTime: LocalDateTime,
                              newInt: Int) =
            sqlClient.updateTable<SqLiteAllTypesNotNull>()
                    .set { it[SqLiteAllTypesNotNull::string] = newString }
                    .set { it[SqLiteAllTypesNotNull::boolean] = newBoolean }
                    .set { it[SqLiteAllTypesNotNull::localDate] = newLocalDate }
                    .set { it[SqLiteAllTypesNotNull::offsetDateTime] = newOffsetDateTime }
                    .set { it[SqLiteAllTypesNotNull::localTime] = newLocalTime }
                    .set { it[SqLiteAllTypesNotNull::localDateTime] = newLocalDateTime }
                    .set { it[SqLiteAllTypesNotNull::int] = newInt }
                    .where { it[SqLiteAllTypesNotNull::id] eq sqLiteAllTypesNotNull.id }
                    .execute()
}
