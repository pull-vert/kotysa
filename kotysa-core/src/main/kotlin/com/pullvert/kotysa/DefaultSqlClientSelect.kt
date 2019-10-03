/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientSelect protected constructor() : DefaultSqlClientCommon() {

    class Properties<T : Any> internal constructor(
            override val tables: Tables,
            val selectInformation: SelectInformation<T>,
            override val availableColumns: MutableMap<(Any) -> Any?, Column<*, *>>
    ) : DefaultSqlClientCommon.Properties {
        override val whereClauses: MutableList<WhereClause> = mutableListOf()
        override val joinClauses: MutableList<JoinClause> = mutableListOf()
    }

    protected interface WithProperties<T : Any> : DefaultSqlClientCommon.WithProperties {
        override val properties: Properties<T>
    }

    @ExperimentalStdlibApi
    @Suppress("UNCHECKED_CAST")
    protected interface Select<T : Any> : Instruction {

        val tables: Tables
        val resultClass: KClass<T>
        val dsl: (SelectDslApi.(ValueProvider) -> T)?

        fun initProperties(): Properties<T> {
            if (dsl == null) {
                tables.checkTable(resultClass)
            }
            val selectInformation = if (dsl != null) {
                SelectDsl(dsl!!, tables).initialize()
            } else {
                selectInformationForSingleClass(resultClass, tables)
            }
            val properties = Properties(tables, selectInformation, mutableMapOf())
            // init availableColumns with all selected tables columns
            selectInformation.selectedTables
                    .forEach { table -> addAvailableColumnsFromTable(properties, table) }
            return properties
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
                            .filterValues { column -> !associatedColumns.contains(column) }
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
            return SelectInformation(fieldIndexMap, selectedFields, setOf(AliasedTable(table)), select)
        }

        private fun valueProviderCall(getter: (T) -> Any?, valueProvider: ValueProvider): Any? =
                when (getter.toCallable().returnType.withNullability(false)) {
                    typeOf<String>() -> valueProvider[getter as (T) -> String?]
                    typeOf<LocalDateTime>() -> valueProvider[getter as (T) -> LocalDateTime?]
                    typeOf<LocalDate>() -> valueProvider[getter as (T) -> LocalDate?]
                    typeOf<OffsetDateTime>() -> valueProvider[getter as (T) -> OffsetDateTime?]
                    typeOf<LocalTime>() -> valueProvider[getter as (T) -> LocalTime?]
                    typeOf<Boolean>() -> valueProvider[getter as (T) -> Boolean]
                    typeOf<UUID>() -> valueProvider[getter as (T) -> UUID?]
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
                    typeOf<String>() ->
                        if (getterType.isMarkedNullable) {
                            NullableStringColumnField(allColumns, getter as (Any) -> String?)
                        } else {
                            NotNullStringColumnField(allColumns, getter as (Any) -> String)
                        }
                    typeOf<LocalDateTime>() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalDateTimeColumnField(allColumns, getter as (Any) -> LocalDateTime?)
                        } else {
                            NotNullLocalDateTimeColumnField(allColumns, getter as (Any) -> LocalDateTime)
                        }
                    typeOf<LocalDate>() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalDateColumnField(allColumns, getter as (Any) -> LocalDate?)
                        } else {
                            NotNullLocalDateColumnField(allColumns, getter as (Any) -> LocalDate)
                        }
                    typeOf<OffsetDateTime>() ->
                        if (getterType.isMarkedNullable) {
                            NullableOffsetDateTimeColumnField(allColumns, getter as (Any) -> OffsetDateTime?)
                        } else {
                            NotNullOffsetDateTimeColumnField(allColumns, getter as (Any) -> OffsetDateTime)
                        }
                    typeOf<LocalTime>() ->
                        if (getterType.isMarkedNullable) {
                            NullableLocalTimeColumnField(allColumns, getter as (Any) -> LocalTime?)
                        } else {
                            NotNullLocalTimeColumnField(allColumns, getter as (Any) -> LocalTime)
                        }
                    typeOf<Boolean>() -> {
                        require(!getterType.isMarkedNullable) { "$getter is nullable, Boolean must not be nullable" }
                        NotNullBooleanColumnField(allColumns, getter as (Any) -> Boolean)
                    }
                    typeOf<UUID>() ->
                        if (getterType.isMarkedNullable) {
                            NullableUuidColumnField(allColumns, getter as (Any) -> UUID?)
                        } else {
                            NotNullUuidColumnField(allColumns, getter as (Any) -> UUID)
                        }
                    else -> throw RuntimeException("should never happen")
                }
                selectedFields.add(field)
                fieldIndexMap[field] = fieldIndex++
            }
            return selectedFields
        }
    }

    protected interface Whereable<T : Any> : DefaultSqlClientCommon.Whereable, WithProperties<T>

    protected interface Join<T : Any> : DefaultSqlClientCommon.Join, WithProperties<T>

    protected interface Where<T : Any> : DefaultSqlClientCommon.Where, WithProperties<T>

    protected interface Return<T : Any> : DefaultSqlClientCommon.Return, WithProperties<T> {
        fun selectSql() = with(properties) {
            val selects = selectInformation.selectedFields.joinToString(prefix = "SELECT ") { field -> field.fieldName }
            val froms = selectInformation.selectedTables
                    .filterNot { aliasedTable -> joinClauses.map { joinClause -> joinClause.table }.contains(aliasedTable) }
                    .joinToString(prefix = "FROM ") { aliasedTable -> aliasedTable.declaration }
            val joins = joins()
            val wheres = wheres()
            logger.debug { "Exec SQL : $selects $froms $joins $wheres" }

            "$selects $froms $joins $wheres"
        }
    }
}

/**
 * @author Fred Montariol
 */
class SelectInformation<T> internal constructor(
        val fieldIndexMap: Map<Field, Int>,
        internal val selectedFields: List<Field>,
        internal val selectedTables: Set<AliasedTable<*>>,
        val select: SelectDslApi.(ValueProvider) -> T
)
