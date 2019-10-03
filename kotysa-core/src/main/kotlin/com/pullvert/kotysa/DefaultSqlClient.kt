/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import mu.KotlinLogging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.allSuperclasses


private fun tableMustBeMapped(tableName: String?) = "Requested table \"$tableName\" is not mapped"

@Suppress("UNCHECKED_CAST")
fun <T : Any> Tables.getTable(tableClass: KClass<out T>): Table<T> =
        requireNotNull(this.allTables[tableClass] as Table<T>?) { tableMustBeMapped(tableClass.qualifiedName) }

fun <T : Any> Tables.checkTable(tableClass: KClass<out T>) {
    require(this.allTables.containsKey(tableClass)) { tableMustBeMapped(tableClass.qualifiedName) }
}

private val logger = KotlinLogging.logger {}

/**
 * @author Fred Montariol
 */
interface DefaultSqlClient {
    val tables: Tables

    fun createTableSql(tableClass: KClass<*>): String {
        val table = tables.getTable(tableClass)
        var primaryKey: String? = null
        val foreignKeys = StringBuilder()
        val columns = table.columns.values.joinToString { column ->
            if (column.isPrimaryKey) {
                primaryKey = "CONSTRAINT PK_${table.name} PRIMARY KEY (${column.name})"
            }
            column.fkColumn?.also { fkColumn ->
                foreignKeys.append(", ")
                foreignKeys.append("CONSTRAINT FK_${fkColumn.table.name} FOREIGN KEY (${column.name}) " +
                        "REFERENCES ${fkColumn.table.name}(${fkColumn.name})")
            }
            val nullability = if (column.isNullable) "NULL" else "NOT NULL"
            "${column.name} ${column.sqlType.fullType} $nullability"
        }
        val createTableSql = "CREATE TABLE IF NOT EXISTS ${table.name} ($columns, $primaryKey$foreignKeys)"
        logger.debug { "Exec SQL : $createTableSql" }
        return createTableSql
    }

    fun <T : Any> insertSql(row: T): String {
        val table = tables.getTable(row::class)
        val columnNames = mutableSetOf<String>()
        val values = table.columns.values.joinToString { column ->
            columnNames.add(column.name)
            "?"
        }

        logger.debug { "Exec SQL : INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($values)" }
        return "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($values)"
    }

    fun <T : Any> insertSqlDebug(row: T) {
        if (logger.isDebugEnabled) {
            val table = tables.getTable(row::class)
            val columnNames = mutableSetOf<String>()
            val valuesDebug = table.columns.values.joinToString { column ->
                columnNames.add(column.name)
                "?"
            }
            logger.debug("Exec SQL : INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($valuesDebug)")
        }
    }
}

private fun Any?.dbValue(): String = when (this) {
    null -> "null"
    is String -> "$this"
    is Boolean -> "$this"
    is UUID -> "$this"
    is LocalDate -> this.format(DateTimeFormatter.ISO_LOCAL_DATE)
    is LocalDateTime -> this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    is LocalTime -> this.format(DateTimeFormatter.ISO_LOCAL_TIME)
    is OffsetDateTime -> this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    else -> throw RuntimeException("${this.javaClass.canonicalName} is not supported yet")
}

/**
 * @author Fred Montariol
 */
open class DefaultSqlClientCommon protected constructor() {

    interface Properties {
        val tables: Tables
        val whereClauses: MutableList<WhereClause>
        val joinClauses: MutableList<JoinClause>
        val availableColumns: MutableMap<(Any) -> Any?, Column<*, *>>
    }

    protected interface Instruction {
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> addAvailableColumnsFromTable(
                properties: Properties,
                table: Table<T>
        ) = properties.apply {
            if (joinClauses.isEmpty() ||
                    !joinClauses.map { joinClause -> joinClause.table.table }.contains(table)) {
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
        }
    }

    interface WithProperties {
        val properties: Properties
    }

    protected interface Whereable : WithProperties

    protected interface Join : WithProperties, Instruction {
        fun <T : Any> addJoinClause(dsl: (FieldProvider) -> ColumnField<*, *>, joinClass: KClass<T>, alias: String?, type: JoinType) {
            properties.apply {
                tables.checkTable(joinClass)
                val aliasedTable = AliasedTable(tables.getTable(joinClass), alias)
                joinClauses.add(JoinDsl(dsl, aliasedTable, type, availableColumns).initialize())
                addAvailableColumnsFromTable(this, aliasedTable)
            }
        }
    }

    protected interface Where : WithProperties {
        fun addWhereClause(dsl: WhereDsl.(FieldProvider) -> WhereClause) {
            properties.apply {
                whereClauses.add(WhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    protected interface TypedWhere<T : Any> : WithProperties {
        fun addWhereClause(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause) {
            properties.apply {
                whereClauses.add(TypedWhereDsl(dsl, availableColumns).initialize())
            }
        }
    }

    interface Return : WithProperties {

        fun stringValue(value: Any?) = value.dbValue()

        fun joins() =
                properties.joinClauses.joinToString { joinClause ->
                    require(joinClause.table.primaryKey is SinglePrimaryKey<*, *>) {
                        "Only table with single column primary key is currently supported, ${joinClause.table.name} is not"
                    }
                    val joinedTableFieldName = "${joinClause.table.prefix}.${joinClause.table.primaryKey.column.name}"

                    "${joinClause.type.sql} ${joinClause.table.declaration} ON ${joinClause.field.fieldName} = $joinedTableFieldName"
                }

        fun wheres(withWhere: Boolean = true): String = with(properties) {
            if (whereClauses.isEmpty()) {
                return ""
            }
            val where = StringBuilder()
            if (withWhere) {
                where.append("WHERE ")
            }
            whereClauses.forEach { whereClause ->
                when (whereClause.operation) {
                    Operation.EQ ->
                        if (whereClause.value == null) {
                            val nullWhereClause = "${whereClause.field.fieldName} IS NULL"
                            where.append(nullWhereClause).append(" AND ")
                        } else {
                            val notNullWhereClause = "${whereClause.field.fieldName} = "
                            where.append(notNullWhereClause).append("?").append(" AND ")
                        }
                    else -> throw UnsupportedOperationException("${whereClause.operation} is not supported yet")
                }
            }
            return where.dropLast(5).toString()
        }
    }
}
