package com.stec

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.stec.validator.DeployConfigValidator
import com.stec.settings.DeploySettingsState
import javax.swing.JButton

class AutoDeployToolWindowFactory : ToolWindowFactory {
    private val deployButton = JButton("Deploy")
    private val clearButton = JButton("Clear Console") // 手动清除按钮

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val settings = DeploySettingsState.getInstance()

        // 创建 ConsoleView，用于显示日志
        val consoleView: ConsoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

        // 配置控制台视图
        consoleView.print("Initializing...\n", ConsoleViewContentType.NORMAL_OUTPUT)

        // 创建并配置布局
        val mainPanel = panel {
            row("Host:") { label(settings.host) }
            row("Environment:") { label(settings.environment) }
            row {
                cell(deployButton)
                    .align(Align.CENTER) // 使用新版对齐方式
            }
            row {
                cell(clearButton) // 添加手动清除按钮
                    .align(Align.CENTER)
            }
            row("Log:") {
                cell(consoleView.component)
                    .align(Align.FILL)
            }.resizableRow() // 该行将变为可调整大小并占据所有垂直可用空间。对于多个可调整大小的行，额外的可用空间将在行之间平均分配。
        }

        // 为按钮添加点击事件
        deployButton.addActionListener {
            deployButton.isEnabled = false  // 禁用部署按钮，防止重复点击
            // 先进行配置验证
            if (DeployConfigValidator.validateConfiguration(settings, consoleView)) {
                ApplicationManager.getApplication().executeOnPooledThread {
                    startDeployment(settings, consoleView)
                    deployButton.isEnabled = true  // 部署完成后重新启用按钮
                }
            } else {
                deployButton.isEnabled = true  // 配置验证失败，重新启用按钮
            }
        }

        // 为清除按钮添加点击事件
        clearButton.addActionListener {
            consoleView.clear() // 清空控制台内容
            consoleView.print("Console cleared.\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        // 将内容添加到 Tool Window
        val content = toolWindow.contentManager.factory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun startDeployment(settings: DeploySettingsState, consoleView: ConsoleView) {
        // 清空控制台
        consoleView.clear()

        consoleView.print("Starting deployment...\n", ConsoleViewContentType.NORMAL_OUTPUT)

        performDeployment(settings, consoleView)
    }

    private fun performDeployment(settings: DeploySettingsState, consoleView: ConsoleView) {
        val deployService = DeployService(consoleView)
        try {
            // 执行部署任务，获取登录时的 Cookie
            val cookie = deployService.login(settings.host, settings.username, settings.password)
            settings.applications.forEach { app ->
                deployService.stopApplication(settings.host, app, settings.environment, cookie)
                deployService.uploadApplication(settings.projectRootPath, settings.host, app, cookie)
                val sourceInfoFile = deployService.searchApplication(settings.host, app, cookie)
                if (sourceInfoFile != null) {
                    deployService.releaseApplication(settings.host, app, settings.environment, sourceInfoFile, cookie)
                }
            }
            consoleView.print("Deployment completed successfully!\n", ConsoleViewContentType.NORMAL_OUTPUT)
        } catch (e: Exception) {
            consoleView.print("Deployment failed: ${e.message}\n", ConsoleViewContentType.ERROR_OUTPUT)
        }
    }
}
