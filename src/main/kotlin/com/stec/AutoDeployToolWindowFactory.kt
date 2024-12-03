package com.stec

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.stec.settings.DeploySettingsState
import javax.swing.JButton
import javax.swing.JProgressBar

class AutoDeployToolWindowFactory : ToolWindowFactory {
    private val deployButton = JButton("Deploy")
    private val clearButton = JButton("Clear Console") // 手动清除按钮
    private val progressBar = JProgressBar(0, 100) // 创建 JProgressBar，范围从 0 到 100

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
            row("Progress:") {
                cell(progressBar)
                    .align(Align.FILL) // 使用新版对齐方式
            }
            row("Log:") {
                cell(consoleView.component)
                    .align(Align.FILL) // 使用新版对齐方式
            }
        }

        // 为按钮添加点击事件
        deployButton.addActionListener {
            startDeployment(settings, consoleView)
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

        // 设置进度条为不确定状态，表示正在执行
        progressBar.isIndeterminate = true
        consoleView.print("Starting deployment...\n", ConsoleViewContentType.NORMAL_OUTPUT)

        // 运行部署任务，并更新进度条
        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            performDeployment(settings, consoleView)
        }, "Deploying Application", true, null)
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
        } finally {
            // 更新进度条为100%
            progressBar.isIndeterminate = false
            progressBar.value = 100

            // 自动滚动到控制台的底部
            consoleView.scrollTo(0) // 确保控制台滚动到最后一行
        }
    }
}
