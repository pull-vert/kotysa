/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KProperty1

/**
 * One database Table's Column model mapped by entity's [entityProperty]
 * @author Fred Montariol
 */
internal interface Column<T : Any, U> {
    var table: Table<T>
    val entityProperty: KProperty1<T, U>
    val columnName: String
    val sqlType: SqlType
    val isPrimaryKey: Boolean
    val isNullable: Boolean
    val defaultValue: Any?
}

/**
 * @author Fred Montariol
 */
enum class SqlType {
    VARCHAR,
    TIMESTAMP,
    DATE;
}

/**
 * @author Fred Montariol
 */
internal interface ColumnNotNull<T : Any, U> : Column<T, U> {
    override val isNullable: Boolean
        get() = false
}

/**
 * @author Fred Montariol
 */
internal interface ColumnNullable<T : Any, U> : Column<T, U> {
    override val isNullable: Boolean
        get() = true

    override val defaultValue: Nothing?
        get() = null

    override val isPrimaryKey: Boolean
        get() = false
}

/**
 * @author Fred Montariol
 */
internal abstract class AbstractColumn<T : Any, U> : Column<T, U> {

    override lateinit var table: Table<T>
}

// String
//interface StringColumn: Column
//interface StringVarcharColumnNotNull<T : StringVarcharColumnNotNull<T>>: StringColumn, ColumnNotNull<T>
//interface StringVarcharColumnNull<T : StringVarcharColumnNull<T>>: StringColumn, ColumnNullable<T>
//
//// LocalDateTime
//interface LocalDateTimeColumn: Column
//interface LocalDateTimeTimestampColumnNotNull<T : LocalDateTimeTimestampColumnNotNull<T>>: LocalDateTimeColumn, ColumnNotNull<T>
//interface LocalDateTimeTimestampColumnNull<T : LocalDateTimeTimestampColumnNull<T>>: LocalDateTimeColumn, ColumnNullable<T>
//
//// Date
//interface JavaDateColumn: Column
//interface JavaDateTimestampColumnNotNull<T : JavaDateTimestampColumnNotNull<T>>: JavaDateColumn, ColumnNotNull<T>
//interface JavaDateTimestampColumnNull<T : JavaDateTimestampColumnNull<T>>: JavaDateColumn, ColumnNullable<T>
//interface JavaDateDateColumnNotNull<T : JavaDateDateColumnNotNull<T>>: JavaDateColumn, ColumnNotNull<T>
//interface JavaDateDateColumnNull<T : JavaDateDateColumnNull<T>>: JavaDateColumn, ColumnNullable<T>

/**
 * @author Fred Montariol
 */
internal interface VarcharColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class VarcharColumnNotNull<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNotNull<T, U>/*, StringVarcharColumnNotNull<VarcharColumnNotNull>*/

/**
 * @author Fred Montariol
 */
internal class VarcharColumnNullable<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), VarcharColumn<T, U>, ColumnNullable<T, U>/*, StringVarcharColumnNull<VarcharColumnNullable>*/

/**
 * @author Fred Montariol
 */
internal interface TimestampColumn<T : Any, U> : Column<T, U>

/**
 * @author Fred Montariol
 */
internal class TimestampColumnNotNull<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNotNull<T, U>/*, LocalDateTimeTimestampColumnNotNull<TimestampColumnNotNull>,
		JavaDateTimestampColumnNotNull<TimestampColumnNotNull>*/ {

    interface ColumnBuilder<U>
        : TimestampColumn.ColumnBuilder<ColumnBuilder<U>>, ColumnNotNull.ColumnBuilder<ColumnBuilder<U>, U>

    class ColumnBuilderImpl<T : Any, U> internal constructor(
            override val entityProperty: KProperty1<T, U>
    ) : TimestampColumn.AbstractColumnBuilder<ColumnBuilder<U>, T>(), ColumnBuilder<U> {

        private var defaultValue: U? = null

        override fun setDefaultValue(defaultValue: U): ColumnBuilder<U> {
            this.defaultValue = defaultValue
            return this
        }

        override val primaryKey = isPrimaryKey

        override fun build() = TimestampColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
    }
}

/**
 * @author Fred Montariol
 */
internal class TimestampColumnNullable<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), TimestampColumn<T, U>, ColumnNullable<T, U>/*, LocalDateTimeTimestampColumnNull<TimestampColumnNull>,
		JavaDateTimestampColumnNull<TimestampColumnNull>*/ {

    interface ColumnBuilder : TimestampColumn.ColumnBuilder<ColumnBuilder>, ColumnNullable.ColumnBuilder<ColumnBuilder>

    class ColumnBuilderImpl<T : Any, U> internal constructor(
            override val entityProperty: KProperty1<T, U>
    ) : TimestampColumn.AbstractColumnBuilder<ColumnBuilder, T>(), ColumnBuilder {
        override fun build() = TimestampColumnNullable(entityProperty, columnName, sqlType)
    }
}

/**
 * @author Fred Montariol
 */
internal interface DateColumn<T : Any, U> : Column<T, U> {

    interface ColumnBuilder<T : ColumnBuilder<T>> : Column.ColumnBuilder<T>

    abstract class AbstractColumnBuilder<T : ColumnBuilder<T>, U : Any> : AbstractColumn.ColumnBuilder<T, U>(), ColumnBuilder<T> {
        override val sqlType = SqlType.DATE
    }
}

/**
 * @author Fred Montariol
 */
internal class DateColumnNotNull<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType,
        override val isPrimaryKey: Boolean,
        override val defaultValue: U?
) : AbstractColumn<T, U>(), DateColumn<T, U>, ColumnNotNull<T, U>/*, JavaDateDateColumnNotNull<DateColumnNotNull>*/ {

    interface ColumnBuilder<U>
        : DateColumn.ColumnBuilder<ColumnBuilder<U>>, ColumnNotNull.ColumnBuilder<ColumnBuilder<U>, U>

    class ColumnBuilderImpl<T : Any, U> internal constructor(
            override val entityProperty: KProperty1<T, U>
    ) : DateColumn.AbstractColumnBuilder<ColumnBuilder<U>, T>(), ColumnBuilder<U> {

        private var defaultValue: U? = null

        override fun setDefaultValue(defaultValue: U): ColumnBuilder<U> {
            this.defaultValue = defaultValue
            return this
        }

        override val primaryKey = isPrimaryKey

        override fun build() = DateColumnNotNull(entityProperty, columnName, sqlType, isPK, defaultValue)
    }
}

/**
 * @author Fred Montariol
 */
internal class DateColumnNullable<T : Any, U> internal constructor(
        override val entityProperty: KProperty1<T, U>,
        override val columnName: String,
        override val sqlType: SqlType
) : AbstractColumn<T, U>(), DateColumn<T, U>, ColumnNullable<T, U>/*, JavaDateDateColumnNull<DateColumnNull>*/ {

    interface ColumnBuilder : DateColumn.ColumnBuilder<ColumnBuilder>, ColumnNullable.ColumnBuilder<ColumnBuilder>

    class ColumnBuilderImpl<T : Any, U> internal constructor(
            override val entityProperty: KProperty1<T, U>
    ) : DateColumn.AbstractColumnBuilder<ColumnBuilder, T>(), ColumnBuilder {
        override fun build() = DateColumnNullable(entityProperty, columnName, sqlType)
    }
}
