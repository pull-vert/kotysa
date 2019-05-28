/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.*
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

/**
 * @author Fred Montariol
 */
open class SqlClientSelect private constructor() {
    interface Select<T : Any> : Return<T> {
        fun where(whereDsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : Return<T>

    interface Return<T : Any>
}

/**
 * @author Fred Montariol
 */
class SqlClientSelectBlocking private constructor() {
    interface Select<T : Any> : SqlClientSelect.Select<T>, Return<T> {
        override fun where(whereDsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T>

    interface Return<T : Any> : SqlClientSelect.Return<T> {
        fun fetchOne(): T
        fun fetchFirst(): T
        fun fetchAll(): List<T>
    }
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientSelect protected constructor() {
    class SelectProperties<T : Any>(
            val tables: Tables,
            val selectInformation: SelectInformation<T>,
            val whereClauses: MutableList<WhereClause>,
            internal val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    )

    @Suppress("UNCHECKED_CAST")
    abstract class Select<T : Any> protected constructor(
            tables: Tables,
            resultClass: KClass<T>,
            selectDsl: ((ValueProvider) -> T)?
    ) : SqlClientSelect.Select<T>, Return<T> {

        final override val selectProperties: SelectProperties<T>

        init {
            if (selectDsl == null) {
                tables.checkTable(resultClass)
            }
            val selectInformation = if (selectDsl != null) {
                SelectDsl(selectDsl, tables.allColumns).initialize()
            } else {
                selectInformationForSingleClass(resultClass, tables)
            }
            // build availableColumns Map
            val availableColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()
            selectInformation.selectedTables
                    .forEach { table ->
                        table.columns.forEach { (key, value) ->
                            // 1) add mapped getter
                            availableColumns[key as (Any) -> Any?] = value

                            val getterCallable = key.toCallable()

                            // 2) add overridden parent getters associated with this column
                            table.tableClass.allSuperclasses
                                    .flatMap { superClass -> superClass.members }
                                    .filter { callable ->
                                        callable.isAbstract
                                                && callable.name == getterCallable.name
                                                && (callable is KProperty1<*, *> || callable.name.startsWith("get"))
                                                && (callable.returnType == getterCallable.returnType
                                                || callable.returnType.classifier is KTypeParameter)
                                    }
                                    .forEach { callable ->
                                        availableColumns[callable as (Any) -> Any?] = value
                                    }
                        }
                    }
            selectProperties = SelectProperties(tables, selectInformation, mutableListOf(), availableColumns)
        }

        @Suppress("UNCHECKED_CAST")
        private fun selectInformationForSingleClass(resultClass: KClass<T>, tables: Tables): SelectInformation<T> {
            val table = tables.allTables[resultClass] as Table<T>
            var fieldIndex = 0
            val columnPropertyIndexMap = mutableMapOf<(Any) -> Any?, Int>()

            // build selectedFields List
            val selectedFields = mutableListOf<Field>()
            table.columns.forEach { (getter, _) ->
                columnPropertyIndexMap[getter as (Any) -> Any?] = fieldIndex++
                val getterType = getter.toCallable().returnType
                val field = when (getterType.withNullability(false)) {
                    String::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullStringColumnField(tables.allColumns, getter as (Any) -> String)
                        } else {
                            NullableStringColumnField(tables.allColumns, getter as (Any) -> String?)
                        }
                    LocalDateTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalDateTimeColumnField(tables.allColumns, getter as (Any) -> LocalDateTime)
                        } else {
                            NullableLocalDateTimeColumnField(tables.allColumns, getter as (Any) -> LocalDateTime?)
                        }
                    LocalDate::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalDateColumnField(tables.allColumns, getter as (Any) -> LocalDate)
                        } else {
                            NullableLocalDateColumnField(tables.allColumns, getter as (Any) -> LocalDate?)
                        }
                    Instant::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullInstantColumnField(tables.allColumns, getter as (Any) -> Instant)
                        } else {
                            NullableInstantColumnField(tables.allColumns, getter as (Any) -> Instant?)
                        }
                    LocalTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NotNullLocalTimeColumnField(tables.allColumns, getter as (Any) -> LocalTime)
                        } else {
                            NullableLocalTimeColumnField(tables.allColumns, getter as (Any) -> LocalTime?)
                        }
                    Boolean::class.createType() -> {
                        require(!getterType.isMarkedNullable) { "$getter is nullable, Boolean must not be nullable" }
                        NotNullBooleanColumnField(tables.allColumns, getter as (Any) -> Boolean)
                    }
                    else -> throw RuntimeException("should never happen")
                }
                selectedFields.add(field)
            }

            // Build select Function : (ValueProvider) -> T
            val select: (ValueProvider) -> T = { it ->

                val constructor = with(table.tableClass) {
                    if (primaryConstructor != null) {
                        primaryConstructor
                    } else {
                        var nbParameters = -1
                        var mostCompleteConstructor: KFunction<T>? = null
                        constructors.forEach { constructor ->
                            if (constructor.parameters.size > nbParameters) {
                                nbParameters = constructor.parameters.size
                                mostCompleteConstructor = constructor
                            }
                        }
                        mostCompleteConstructor
                    }
                }
                with(constructor!!) {
                    val args = mutableMapOf<KParameter, Any?>()
                    parameters.forEach { param ->
                        // get the mapped property with same name
                        val getter = table.columns.keys.firstOrNull { getter ->
                            var getterMatch = false
                            val getterName = getter.toCallable().name
                            if (getterName.startsWith("get") && getterName.length > 3) {
                                if (getterName.substring(3).toLowerCase() == param.name!!.toLowerCase()) {
                                    getterMatch = true
                                }
                            }
                            getterMatch || (getterName == param.name)
                        }
                        if (getter != null) {
                            when (getter.toCallable().returnType.withNullability(false)) {
                                String::class.createType() -> args[param] = it[getter as (Any) -> String?]
                                LocalDateTime::class.createType() -> args[param] = it[getter as (Any) -> LocalDateTime?]
                                LocalDate::class.createType() -> args[param] = it[getter as (Any) -> LocalDate?]
                                Instant::class.createType() -> args[param] = it[getter as (Any) -> Instant?]
                                LocalTime::class.createType() -> args[param] = it[getter as (Any) -> LocalTime?]
                                Boolean::class.createType() -> args[param] = it[getter as (Any) -> Boolean]
                                else -> throw RuntimeException("should never happen")
                            }
                        } else {
                            require(param.isOptional) {
                                "Cannot instanciate Table \"${table.tableClass.qualifiedName}\"," +
                                        "parameter \"${param.name}\" is required and is not mapped to a Column"
                            }
                        }
                    }
                    // invoke constructor
                    callBy(args)
                }
            }
            return SelectInformation(columnPropertyIndexMap, selectedFields, setOf(table), select)
        }
    }

    protected interface Where<T : Any> : SqlClientSelect.Where<T>, Return<T> {

        fun addWhereClause(dsl: WhereDsl<T>.(WhereFieldProvider) -> WhereClause) {
            selectProperties.apply {
                whereClauses.add(WhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    protected interface Return<T : Any> : SqlClientSelect.Return<T> {
        val selectProperties: SelectProperties<T>

        fun selectSql(): String = with(selectProperties) {
            val selects = selectInformation.selectedFields.joinToString(prefix = "SELECT ") { field -> field.fieldName }
            val froms = selectInformation.selectedTables.joinToString(prefix = "FROM ") { table -> table.name }
            val wheres = if (whereClauses.isEmpty()) {
                ""
            } else {
                whereClauses.joinToString(" AND ", "WHERE ") { whereClause ->
                    when (whereClause.operation) {
                        Operation.EQ ->
                            if (whereClause.value == null) {
                                "${whereClause.field.fieldName} IS NULL"
                            } else {
                                "${whereClause.field.fieldName} = ?"
                            }
                        else -> throw RuntimeException("should never happen")
                    }
                }
            }
            if (logger.isDebugEnabled) {
                val wheresDebug = if (whereClauses.isEmpty()) {
                    ""
                } else {
                    whereClauses.joinToString(" AND ", "WHERE ") { whereClause ->
                        when (whereClause.operation) {
                            Operation.EQ ->
                                if (whereClause.value == null) {
                                    "${whereClause.field.fieldName} IS NULL"
                                } else {
                                    "${whereClause.field.fieldName} = ${logValue(whereClause.value)}"
                                }
                            else -> throw RuntimeException("should never happen")
                        }
                    }
                }
                logger.debug { "Exec SQL : $selects $froms $wheresDebug" }
            }
            return "$selects $froms $wheres"
        }
    }
}

/**
 * @author Fred Montariol
 */
data class SelectInformation<T>(
        val columnPropertyIndexMap: Map<out (Any) -> Any?, Int>,
        internal val selectedFields: List<Field>,
        internal val selectedTables: Set<Table<*>>,
        val select: (ValueProvider) -> T
)
