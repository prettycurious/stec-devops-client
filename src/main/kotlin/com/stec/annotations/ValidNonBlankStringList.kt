package com.stec.annotations

import com.stec.validator.ListStringValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ListStringValidator::class])  // 指定验证器
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidNonBlankStringList(
    val message: String = "Each string in the list must be non-blank",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)