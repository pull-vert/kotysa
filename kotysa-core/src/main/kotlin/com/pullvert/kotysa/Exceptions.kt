/*
 * Copyright 2019-2020 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa

/**
 * @author Fred Montariol
 */
public class NonUniqueResultException : RuntimeException("Multiple results, query expected a single result")

/**
 * @author Fred Montariol
 */
public class NoResultException : RuntimeException("No result, query expected a result")
