package com.pullvert.kotysa.annotations

import javax.annotation.Nullable
import javax.annotation.meta.TypeQualifierNickname
import kotlin.annotation.AnnotationRetention.RUNTIME

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(RUNTIME)
@MustBeDocumented
@Nullable
@TypeQualifierNickname
annotation class Nullable
