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
interface WhereColumnPropertyProvider {
    operator fun <T : Any> get(stringProperty: KProperty1<T, String>, alias: String? = null): NotNullStringColumnProperty<T>

    operator fun <T : Any> get(localDateTimeProperty: KProperty1<T, LocalDateTime>, alias: String? = null): NotNullLocalDateTimeColumnProperty<T>

    operator fun <T : Any> get(dateProperty: KProperty1<T, Date>, alias: String? = null): NotNullDateColumnProperty<T>

    operator fun <T : Any> get(nullableStringProperty: KProperty1<T, String?>, alias: String? = null): NullableStringColumnProperty<T>

    operator fun <T : Any> get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>, alias: String? = null): NullableLocalDateTimeColumnProperty<T>

    operator fun <T : Any> get(nullableDateProperty: KProperty1<T, Date?>, alias: String? = null): NullableDateColumnProperty<T>
}

/**
 * @author Fred Montariol
 */
internal class WhereColumnPropertyProviderImpl : ColumnPropertyProvider(), WhereColumnPropertyProvider {
    override fun <T : Any> get(stringProperty: KProperty1<T, String>, alias: String?) = colProp(stringProperty, alias)

    override fun <T : Any> get(localDateTimeProperty: KProperty1<T, LocalDateTime>, alias: String?) =
            colProp(localDateTimeProperty, alias)

    override fun <T : Any> get(dateProperty: KProperty1<T, Date>, alias: String?) = colProp(dateProperty, alias)

    override fun <T : Any> get(nullableStringProperty: KProperty1<T, String?>, alias: String?) =
            colProp(nullableStringProperty, alias)

    override fun <T : Any> get(nullableLocalDateTimeProperty: KProperty1<T, LocalDateTime?>, alias: String?) =
            colProp(nullableLocalDateTimeProperty, alias)

    override fun <T : Any> get(nullableDateProperty: KProperty1<T, Date?>, alias: String?) =
            colProp(nullableDateProperty, alias)

}
