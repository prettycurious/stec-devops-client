package com.stec.validator

import com.stec.annotations.ValidNonBlankStringList
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ListStringValidator : ConstraintValidator<ValidNonBlankStringList, List<String>> {

    override fun initialize(constraintAnnotation: ValidNonBlankStringList) {
        // 初始化校验器
    }

    override fun isValid(value: List<String>?, context: ConstraintValidatorContext?): Boolean {
        // 校验逻辑
        if (value.isNullOrEmpty()) {
            return false
        }
        // 检查每个字符串是否为空
        return value.all { it.isNotBlank() }
    }
}