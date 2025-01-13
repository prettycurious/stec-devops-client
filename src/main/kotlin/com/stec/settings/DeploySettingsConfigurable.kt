package com.stec.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class DeploySettingsConfigurable(project: Project) : Configurable {
    private val settings = DeploySettingsState.getInstance(project)
    private val hostField = JBTextField(settings.host)
    private val usernameField = JBTextField(settings.username)
    private val passwordField = JBTextField(settings.password)
    private val releasePathField = JBTextField(settings.releasePath)
    private val environmentField = JBTextField(settings.environment)
    private val projectRootPathField = JBTextField(settings.projectRootPath)
    private val applicationsField = JBTextField(settings.applications.joinToString(","))

    override fun getDisplayName(): String = "Auto Deploy Settings"

    override fun createComponent(): JComponent = panel {
        row("Server Host:") { cell(hostField) }
        row("Username:") { cell(usernameField) }
        row("Password:") { cell(passwordField) }
        row("Release Path:") { cell(releasePathField) }
        row("Environment:") { cell(environmentField) }
        row("Project Root Path:") { cell(projectRootPathField) }
        row("Applications (comma-separated):") { cell(applicationsField) }
    }

    override fun isModified(): Boolean {
        return hostField.text != settings.host ||
                usernameField.text != settings.username ||
                passwordField.text != settings.password ||
                releasePathField.text != settings.releasePath ||
                environmentField.text != settings.environment ||
                projectRootPathField.text != settings.projectRootPath ||
                applicationsField.text.split(",").map { it.trim() } != settings.applications
    }

    override fun apply() {
        settings.host = hostField.text
        settings.username = usernameField.text
        settings.password = passwordField.text
        settings.releasePath = releasePathField.text
        settings.environment = environmentField.text
        settings.projectRootPath = projectRootPathField.text
        settings.applications = applicationsField.text.split(",").map { it.trim() }
    }

    override fun reset() {
        hostField.text = settings.host
        usernameField.text = settings.username
        passwordField.text = settings.password
        releasePathField.text = settings.releasePath
        environmentField.text = settings.environment
        projectRootPathField.text = settings.projectRootPath
        applicationsField.text = settings.applications.joinToString(",")
    }
}
