/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

import kotlin.reflect.KProperty1

/**
 * @author Fred Montariol
 */
abstract class WhereClause<T : Any, U> {
    internal abstract val entityProperty: KProperty1<T, U>
}
