package com.stec.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class RunAutoDeployAction : AnAction("Run Auto Deploy") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        Messages.showInfoMessage(project, "Starting Auto Deploy...", "Info")
        // 调用部署逻辑（可以集成上面工具窗口的逻辑）
    }
}
