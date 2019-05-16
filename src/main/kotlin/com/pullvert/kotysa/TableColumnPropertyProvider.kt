/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
interface TableColumnPropertyProvider<T : Any> {
    operator fun get(stringProperty: KProperty1<T, String>): NotNullStringColumnProperty<T>

    operator fun get(localDateTimeProperty: KProperty1<T, LocalDateTime>): NotNullLocalDateTimeColumnProperty<T>

    operator fun get(dateProperty: KProperty1<T, Date>): NotNullDateColumnProperty<T>

    operator fun get(nullableStringProperty: KProperty1<T, String?>): NullableStringColumnProperty<T>

    operator fun get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>): NullableLocalDateTimeColumnProperty<T>

    operator fun get(nullableDateProperty: KProperty1<T, Date?>): NullableDateColumnProperty<T>
}

/**
 * @author Fred Montariol
 */
internal class TableColumnPropertyProviderImpl<T : Any> : ColumnPropertyProvider(), TableColumnPropertyProvider<T> {
    override fun get(stringProperty: KProperty1<T, String>) = colProp(stringProperty)

    override fun get(localDateTimeProperty: KProperty1<T, LocalDateTime>) = colProp(localDateTimeProperty)

    override fun get(dateProperty: KProperty1<T, Date>) = colProp(dateProperty)

    override fun get(nullableStringProperty: KProperty1<T, String?>) = colProp(nullableStringProperty)

    override fun get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>) = colProp(nullableLocalDateTimeProperty)

    override fun get(nullableDateProperty: KProperty1<T, Date?>) = colProp(nullableDateProperty)
}
