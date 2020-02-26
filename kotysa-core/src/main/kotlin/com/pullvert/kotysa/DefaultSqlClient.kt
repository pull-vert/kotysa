/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import com.github.michaelbull.logging.InlineLogger
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

private val logger = InlineLogger("com.pullvert.kotysa.DefaultSqlClient")

/**
 * @author Fred Montariol
 */
interface DefaultSqlClient {
    val tables: Tables

    fun createTableSql(tableClass: KClass<*>): String {
        val table = tables.getTable(tableClass)

        val columns = table.columns.values.joinToString { column ->
            val nullability = if (column.isNullable) "NULL" else "NOT NULL"
            val autoIncrement = if (column.isAutoIncrement && DbType.SQLITE != tables.dbType) {
                // SQLITE : The AUTOINCREMENT keyword imposes extra CPU, memory, disk space, and disk I/O overhead and should be avoided if not strictly needed.
                // It is usually not needed -> https://sqlite.org/autoinc.html
                // if this needs to be added later, sqlite syntax MUST be "column INTEGER PRIMARY KEY AUTOINCREMENT"
                " AUTO_INCREMENT"
            } else {
                ""
            }
            val default = if (column.defaultValue != null) {
                " DEFAULT ${column.defaultValue.defaultValue()}"
            } else {
                ""
            }
            "${column.name} ${column.sqlType.fullType} $nullability$autoIncrement$default"
        }

        val primaryKey = when (val primaryKey = table.primaryKey) {
            is SinglePrimaryKey<*, *> ->
                if (primaryKey.name != null) {
                    "CONSTRAINT ${primaryKey.name} PRIMARY KEY (${primaryKey.column.name})"
                } else {
                    "PRIMARY KEY (${primaryKey.column.name})"
                }
            else -> throw RuntimeException("Only SinglePrimaryKey is currently supported, had ${primaryKey::class.simpleName}")
        }

        val foreignKeys =
                if (table.foreignKeys.isEmpty()) {
                    ""
                } else {
                    table.foreignKeys.joinToString(prefix = ", ") { foreignKey ->
                        when (foreignKey) {
                            is SingleForeignKey<*, *> ->
                                if (foreignKey.name != null) {
                                    "CONSTRAINT ${foreignKey.name} FOREIGN KEY (${foreignKey.column.name}) " +
                                            "REFERENCES ${foreignKey.referencedColumn.table.name}(${foreignKey.referencedColumn.name})"
                                } else {
                                    "FOREIGN KEY (${foreignKey.column.name}) " +
                                            "REFERENCES ${foreignKey.referencedColumn.table.name}(${foreignKey.referencedColumn.name})"
                                }
                            else -> throw RuntimeException("Only SingleForeignKey is currently supported, had ${foreignKey::class.simpleName}")
                        }
                    }
                }

        val createTableSql = "CREATE TABLE IF NOT EXISTS ${table.name} ($columns, $primaryKey$foreignKeys)"
        logger.debug { "Exec SQL : $createTableSql" }
        return createTableSql
    }

    fun checkRowsAreMapped(vararg rows: Any) {
        // fail-fast : check that all tables are mapped Tables
        rows.forEach { row -> tables.checkTable(row::class) }
    }

    fun <T : Any> insertSql(row: T): String {
        val insertSqlQuery = insertSqlQuery(row)
        logger.debug { "Exec SQL : $insertSqlQuery" }
        return insertSqlQuery
    }

    fun <T : Any> insertSqlDebug(row: T) {
        logger.debug { "Exec SQL : ${insertSqlQuery(row)}" }
    }

    fun <T : Any> insertSqlQuery(row: T): String {
        val table = tables.getTable(row::class)
        val columnNames = mutableSetOf<String>()
        var index = 1
        val values = table.columns.values
                // filter out null values with default
                .filterNot { column -> column.entityGetter(row) == null && column.defaultValue != null }
                .joinToString { column ->
                    columnNames.add(column.name)
                    if (DbType.POSTGRESQL == tables.dbType) {
                        "$${index++}"
                    } else {
                        "?"
                    }
                }

        return "INSERT INTO ${table.name} (${columnNames.joinToString()}) VALUES ($values)"
    }
}

private fun Any?.dbValue(): String = when (this) {
    null -> "null"
    is String -> "$this"
    is Boolean -> "$this"
    is UUID -> "$this"
    is Int -> "$this"
    is LocalDate -> this.format(DateTimeFormatter.ISO_LOCAL_DATE)
    is LocalDateTime -> this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    is LocalTime -> this.format(DateTimeFormatter.ISO_LOCAL_TIME)
    is OffsetDateTime -> this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    else -> throw RuntimeException("${this.javaClass.canonicalName} is not supported yet")
}

private fun Any?.defaultValue(): String = when (this) {
    is Int -> "$this"
    else -> "'${this.dbValue()}'"
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
                joinClauses.add(JoinDsl(dsl, aliasedTable, type, availableColumns, tables.dbType).initialize())
                addAvailableColumnsFromTable(this, aliasedTable)
            }
        }
    }

    protected interface Where : WithProperties {
        fun addWhereClause(dsl: WhereDsl.(FieldProvider) -> WhereClause) {
            properties.apply {
                whereClauses.add(WhereDsl(dsl, availableColumns, tables.dbType).initialize())
            }
        }
    }

    protected interface TypedWhere<T : Any> : WithProperties {
        fun addWhereClause(dsl: TypedWhereDsl<T>.(TypedFieldProvider<T>) -> WhereClause) {
            properties.apply {
                whereClauses.add(TypedWhereDsl(dsl, availableColumns, tables.dbType).initialize())
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

        fun wheres(withWhere: Boolean = true, offset: Int = 1): String = with(properties) {
            if (whereClauses.isEmpty()) {
                return ""
            }
            val where = StringBuilder()
            if (withWhere) {
                where.append("WHERE ")
            }
            var index = offset
            whereClauses.forEach { whereClause ->
                where.append(
                        when (whereClause.operation) {
                            Operation.EQ ->
                                if (whereClause.value == null) {
                                    "${whereClause.field.fieldName} IS NULL AND "
                                } else {
                                    if (DbType.POSTGRESQL == tables.dbType) {
                                        "${whereClause.field.fieldName} = $${index++} AND "
                                    } else {
                                        "${whereClause.field.fieldName} = ? AND "
                                    }
                                }
                            Operation.NOT_EQ ->
                                if (whereClause.value == null) {
                                    "${whereClause.field.fieldName} IS NOT NULL AND "
                                } else {
                                    if (DbType.POSTGRESQL == tables.dbType) {
                                        "${whereClause.field.fieldName} <> $${index++} AND "
                                    } else {
                                        "${whereClause.field.fieldName} <> ? AND "
                                    }
                                }
                            Operation.CONTAINS, Operation.STARTS_WITH, Operation.ENDS_WITH ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} LIKE $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} LIKE ? AND "
                                }
                            Operation.INF ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} < $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} < ? AND "
                                }
                            Operation.INF_OR_EQ ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} <= $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} <= ? AND "
                                }
                            Operation.SUP ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} <= $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} > ? AND "
                                }
                            Operation.SUP_OR_EQ ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} >= $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} >= ? AND "
                                }
                            Operation.IS ->
                                if (DbType.POSTGRESQL == tables.dbType) {
                                    "${whereClause.field.fieldName} IS $${index++} AND "
                                } else {
                                    "${whereClause.field.fieldName} IS ? AND "
                                }
                            else -> throw UnsupportedOperationException("${whereClause.operation} is not supported yet")
                        }
                )
            }
            return where.dropLast(5).toString()
        }
    }
}
