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
import kotlin.reflect.full.*

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
        private val selectInformation: SelectInformation<T>

        init {
            if (selectDsl == null) {
                tables.checkTable(resultClass)
            }
            selectInformation = if (selectDsl != null) {
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
            val columnPropertyIndexMap = mutableMapOf<(Any) -> Any?, Int>()

            // build selectedFields List & fill columnPropertyIndexMap
            val selectedFields = selectedFieldsFromTable(table.columns, columnPropertyIndexMap, tables.allColumns)

            // Build select Function : (ValueProvider) -> T
            val select: (ValueProvider) -> T = { it ->
                val associatedColumns = mutableListOf<Column<*, *>>()
                val constructor = getTableConstructor(table.tableClass)
                val instance = with(constructor!!) {
                    val args = mutableMapOf<KParameter, Any?>()
                    parameters.forEach { param ->
                        // get the mapped property with same name
                        val columnEntry = table.columns.entries.firstOrNull { columnEntry ->
                            var getterMatch = false
                            val getterName = columnEntry.key.toCallable().name
                            if (getterName.startsWith("get") && getterName.length > 3) {
                                if (getterName.substring(3).toLowerCase() == param.name!!.toLowerCase()) {
                                    getterMatch = true
                                }
                            }
                            val matchFound = getterMatch || (getterName == param.name)
                            if (matchFound) {
                                associatedColumns.add(columnEntry.value)
                            }
                            matchFound
                        }
                        if (columnEntry != null) {
                            args[param] = valueProviderCall(columnEntry.key, it)
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

                // Then try to invoke var or setter for each unassociated getter
                if (associatedColumns.size < table.columns.size) {
                    table.columns
                            .filter { (_, column) -> !associatedColumns.contains(column) }
                            .forEach { (getter, column) ->
                                if (getter is KMutableProperty1<T, Any?>) {
                                    getter.set(instance, valueProviderCall(getter, it))
                                    associatedColumns.add(column)
                                } else {
                                    val callable = getter.toCallable()
                                    if (callable is KFunction<Any?>
                                            && (callable.name.startsWith("get")
                                                    || callable.name.startsWith("is"))
                                            && callable.name.length > 3) {
                                        // try to find setter
                                        val setter = if (callable.name.startsWith("get")) {
                                            table.tableClass.memberFunctions.firstOrNull { function ->
                                                function.name == callable.name.replaceFirst("g", "s")
                                                        && function.parameters.size == 2
                                            }
                                        } else {
                                            // then "is" for Boolean
                                            table.tableClass.memberFunctions.firstOrNull { function ->
                                                function.name == callable.name.replaceFirst("is", "set")
                                                        && function.parameters.size == 2
                                            }
                                        }
                                        if (setter != null) {
                                            setter.call(instance, valueProviderCall(getter, it))
                                            associatedColumns.add(column)
                                        }
                                    }
                                }
                            }
                }
                instance
            }
            return SelectInformation(columnPropertyIndexMap, selectedFields, setOf(table), select)
        }

        private fun valueProviderCall(getter: (T) -> Any?, valueProvider: ValueProvider): Any? =
                when (getter.toCallable().returnType.withNullability(false)) {
                    String::class.createType() -> valueProvider[getter as (T) -> String?]
                    LocalDateTime::class.createType() -> valueProvider[getter as (T) -> LocalDateTime?]
                    LocalDate::class.createType() -> valueProvider[getter as (T) -> LocalDate?]
                    Instant::class.createType() -> valueProvider[getter as (T) -> Instant?]
                    LocalTime::class.createType() -> valueProvider[getter as (T) -> LocalTime?]
                    Boolean::class.createType() -> valueProvider[getter as (T) -> Boolean]
                    else -> throw RuntimeException("should never happen")
                }

        private fun getTableConstructor(tableClass: KClass<T>) = with(tableClass) {
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

        private fun selectedFieldsFromTable(
                columns: Map<(T) -> Any?, Column<T, *>>,
                columnPropertyIndexMap: MutableMap<(Any) -> Any?, Int>,
                allColumns: Map<out (Any) -> Any?, Column<*, *>>
        ): List<Field> {
            var fieldIndex = 0
            val selectedFields = mutableListOf<Field>()
            columns.forEach { (getter, _) ->
                columnPropertyIndexMap[getter as (Any) -> Any?] = fieldIndex++
                val getterType = getter.toCallable().returnType
                val field = when (getterType.withNullability(false)) {
                    String::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NullableStringColumnField(allColumns, getter as (Any) -> String?)
                        } else {
                            NotNullStringColumnField(allColumns, getter as (Any) -> String)
                        }
                    LocalDateTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalDateTimeColumnField(allColumns, getter as (Any) -> LocalDateTime?)
                        } else {
                            NotNullLocalDateTimeColumnField(allColumns, getter as (Any) -> LocalDateTime)
                        }
                    LocalDate::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalDateColumnField(allColumns, getter as (Any) -> LocalDate?)
                        } else {
                            NotNullLocalDateColumnField(allColumns, getter as (Any) -> LocalDate)
                        }
                    Instant::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NullableInstantColumnField(allColumns, getter as (Any) -> Instant?)
                        } else {
                            NotNullInstantColumnField(allColumns, getter as (Any) -> Instant)
                        }
                    LocalTime::class.createType() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalTimeColumnField(allColumns, getter as (Any) -> LocalTime?)
                        } else {
                            NotNullLocalTimeColumnField(allColumns, getter as (Any) -> LocalTime)
                        }
                    Boolean::class.createType() -> {
                        require(!getterType.isMarkedNullable) { "$getter is nullable, Boolean must not be nullable" }
                        NotNullBooleanColumnField(allColumns, getter as (Any) -> Boolean)
                    }
                    else -> throw RuntimeException("should never happen")
                }
                selectedFields.add(field)
            }
            return selectedFields
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
