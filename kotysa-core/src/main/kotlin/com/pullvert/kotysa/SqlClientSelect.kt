/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

/**
 * @author Fred Montariol
 */
open class SqlClientSelect private constructor() {
    interface Select<T : Any> : Return<T> {
        fun where(dsl: WhereDsl.(FieldProvider) -> WhereClause): Where<T>
    }

    interface Where<T : Any> : Return<T>

    interface Return<T : Any>
}


private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientSelect protected constructor() : DefaultSqlClientCommon() {
    class Properties<T : Any>(
            override val tables: Tables,
            val selectInformation: SelectInformation<T>,
            override val whereClauses: MutableList<WhereClause>,
            override val availableColumns: MutableMap<out (Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties

    @Suppress("UNCHECKED_CAST")
    abstract class Select<T : Any> protected constructor(
            tables: Tables,
            resultClass: KClass<T>,
            dsl: (SelectDslApi.(ValueProvider) -> T)?
    ) : DefaultSqlClientCommon.Instruction(), SqlClientSelect.Select<T>, Return<T> {

        final override val properties: Properties<T>
        private val selectInformation: SelectInformation<T>

        init {
            if (dsl == null) {
                tables.checkTable(resultClass)
            }
            selectInformation = if (dsl != null) {
                SelectDsl(dsl, tables).initialize()
            } else {
                selectInformationForSingleClass(resultClass, tables)
            }
            // build availableColumns Map
            val availableColumns = mutableMapOf<(Any) -> Any?, Column<*, *>>()
            selectInformation.selectedTables
                    .forEach { table -> addAvailableColumnsFromTable(availableColumns, table) }
            properties = Properties(tables, selectInformation, mutableListOf(), availableColumns)
        }

        @Suppress("UNCHECKED_CAST")
        private fun selectInformationForSingleClass(resultClass: KClass<T>, tables: Tables): SelectInformation<T> {
            val table = tables.allTables[resultClass] as Table<T>
            val fieldIndexMap = mutableMapOf<Field, Int>()

            // build selectedFields List & fill columnPropertyIndexMap
            val selectedFields = selectedFieldsFromTable(table.columns, fieldIndexMap, tables.allColumns)

            // Build select Function : (ValueProvider) -> T
            val select: SelectDslApi.(ValueProvider) -> T = { it ->
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
            return SelectInformation(fieldIndexMap, selectedFields, setOf(table), select)
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
                fieldIndexMap: MutableMap<Field, Int>,
                allColumns: Map<out (Any) -> Any?, Column<*, *>>
        ): List<Field> {
            var fieldIndex = 0
            val selectedFields = mutableListOf<Field>()
            columns.forEach { (getter, _) ->
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
                fieldIndexMap[field] = fieldIndex++
            }
            return selectedFields
        }
    }

    protected interface Where<T : Any> : DefaultSqlClientCommon.Where, SqlClientSelect.Where<T>, Return<T>

    protected interface Return<T : Any> : DefaultSqlClientCommon.Return, SqlClientSelect.Return<T> {
        override val properties: Properties<T>

        fun selectSql() = with(properties) {
            val selects = selectInformation.selectedFields.joinToString(prefix = "SELECT ") { field -> field.fieldName }
            val froms = selectInformation.selectedTables.joinToString(prefix = "FROM ") { table -> table.name }
            val whereAndWhereDebug = whereAndWhereDebug(whereClauses, logger)
            logger.debug { "Exec SQL : $selects $froms ${whereAndWhereDebug.second}" }

            "$selects $froms ${whereAndWhereDebug.first}"
        }
    }
}

/**
 * @author Fred Montariol
 */
data class SelectInformation<T>(
        val fieldIndexMap: Map<Field, Int>,
        internal val selectedFields: List<Field>,
        internal val selectedTables: Set<Table<*>>,
        val select: SelectDslApi.(ValueProvider) -> T
)
