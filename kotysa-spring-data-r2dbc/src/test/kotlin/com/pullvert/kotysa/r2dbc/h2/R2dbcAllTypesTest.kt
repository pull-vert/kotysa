/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc.h2

import com.pullvert.kotysa.r2dbc.Repository
import com.pullvert.kotysa.r2dbc.sqlClient
import com.pullvert.kotysa.test.common.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

/**
 * @author Fred Montariol
 */
class R2dbcAllTypesTest : AbstractR2dbcTest<AllTypesRepository>() {
	override val context = startContext<AllTypesRepository>()

	override val repository = getContextRepository<AllTypesRepository>()

	@Test
	fun `Verify selectAllAllTypesNotNull returns all AllTypesNotNull`() {
		assertThat(repository.selectAllAllTypesNotNull().toIterable())
				.hasSize(1)
				.containsExactly(h2AllTypesNotNull)
	}

	@Test
	fun `Verify selectAllAllTypesNullable returns all AllTypesNullable`() {
		assertThat(repository.selectAllAllTypesNullable().toIterable())
				.hasSize(1)
				.containsExactly(h2AllTypesNullable)
	}

	@Test
	fun `Verify updateAll works`() {
		val newLocalDate = LocalDate.now()
		val newOffsetDateTime = OffsetDateTime.now()
		val newLocalTime = LocalTime.now()
		val newLocalDateTime = LocalDateTime.now()
		val newUuid = UUID.randomUUID()
		repository.updateAllTypesNotNull("new", false, newLocalDate, newOffsetDateTime, newLocalTime,
				newLocalDateTime, newLocalDateTime, newUuid).block()
		assertThat(repository.selectAllAllTypesNotNull().toIterable())
				.hasSize(1)
				.containsExactlyInAnyOrder(
						H2AllTypesNotNull(h2AllTypesNotNull.id, "new", false, newLocalDate, newOffsetDateTime,
								newLocalTime, newLocalDateTime, newLocalDateTime, newUuid))
		repository.updateAllTypesNotNull(h2AllTypesNotNull.string, h2AllTypesNotNull.boolean, h2AllTypesNotNull.localDate,
				h2AllTypesNotNull.offsetDateTime, h2AllTypesNotNull.localTim, h2AllTypesNotNull.localDateTime1,
				h2AllTypesNotNull.localDateTime2, h2AllTypesNotNull.uuid).block()
	}
}

/**
 * @author Fred Montariol
 */
class AllTypesRepository(dbClient: DatabaseClient) : Repository {

	private val sqlClient = dbClient.sqlClient(h2Tables)

	override fun init() {
		createTables()
				.then(insertAllTypes())
				.block()
	}

	override fun delete() {
		deleteAllFromAllTypesNotNull()
				.then(deleteAllFromAllTypesNullable())
				.block()
	}

	fun createTables() =
			sqlClient.createTable<H2AllTypesNotNull>()
					.then(sqlClient.createTable<H2AllTypesNullable>())

	fun insertAllTypes() = sqlClient.insert(h2AllTypesNotNull, h2AllTypesNullable)

	fun deleteAllFromAllTypesNotNull() = sqlClient.deleteAllFromTable<H2AllTypesNotNull>()

	fun deleteAllFromAllTypesNullable() = sqlClient.deleteAllFromTable<H2AllTypesNullable>()

	fun selectAllAllTypesNotNull() = sqlClient.selectAll<H2AllTypesNotNull>()

	fun selectAllAllTypesNullable() = sqlClient.selectAll<H2AllTypesNullable>()

	fun updateAllTypesNotNull(newString: String, newBoolean: Boolean, newLocalDate: LocalDate,
							  newOffsetDateTime: OffsetDateTime, newLocalTim: LocalTime, newLocalDateTime1: LocalDateTime,
							  newLocalDateTime2: LocalDateTime, newUuid: UUID) =
			sqlClient.updateTable<H2AllTypesNotNull>()
					.set { it[H2AllTypesNotNull::string] = newString }
					.set { it[H2AllTypesNotNull::boolean] = newBoolean }
					.set { it[H2AllTypesNotNull::localDate] = newLocalDate }
					.set { it[H2AllTypesNotNull::offsetDateTime] = newOffsetDateTime }
					.set { it[H2AllTypesNotNull::localTim] = newLocalTim }
					.set { it[H2AllTypesNotNull::localDateTime1] = newLocalDateTime1 }
					.set { it[H2AllTypesNotNull::localDateTime2] = newLocalDateTime2 }
					.set { it[H2AllTypesNotNull::uuid] = newUuid }
					.where { it[H2AllTypesNotNull::id] eq h2AllTypesNotNull.id }
					.execute()
}
