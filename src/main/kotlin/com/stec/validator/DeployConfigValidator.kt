package com.stec.validator

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.stec.settings.DeploySettingsState
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory

object DeployConfigValidator {

    private val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
    private val validator: Validator = validatorFactory.validator

    /**
     * 验证配置项
     * @return 如果配置有效返回 true，否则返回 false
     */
    fun validateConfiguration(settings: DeploySettingsState, consoleView: ConsoleView): Boolean {
        // 清空控制台内容
        consoleView.clear()

        // 验证配置项
        val violations = validator.validate(settings)

        if (violations.isNotEmpty()) {
            // 输出错误信息
            violations.forEach { violation ->
                consoleView.print("Error: ${violation.message}\n", ConsoleViewContentType.ERROR_OUTPUT)
            }
            return false
        }

        // 所有配置项验证通过
        consoleView.print("Configuration validation passed.\n", ConsoleViewContentType.NORMAL_OUTPUT)
        return true
    }
}
