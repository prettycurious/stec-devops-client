package com.stec.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.stec.annotations.ValidNonBlankStringList
import jakarta.validation.constraints.NotBlank
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

@State(name = "DeploySettingsState", storages = [Storage("DeploySettings.xml")])
@Service(Service.Level.PROJECT)
class DeploySettingsState : PersistentStateComponent<DeploySettingsState> {

    private val propertyChangeSupport = PropertyChangeSupport(this)

    @field:NotBlank(message = "host cannot be blank.")
    var host: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("host", oldValue, value)
        }

    @field:NotBlank(message = "username cannot be blank.")
    var username: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("username", oldValue, value)
        }

    @field:NotBlank(message = "password cannot be blank.")
    var password: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("password", oldValue, value)
        }

    @field:NotBlank(message = "releasePath cannot be blank.")
    var releasePath: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("releasePath", oldValue, value)
        }

    @field:NotBlank(message = "environment cannot be blank.")
    var environment: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("environment", oldValue, value)
        }

    @field:NotBlank(message = "projectRootPath cannot be blank.")
    var projectRootPath: String = ""
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("projectRootPath", oldValue, value)
        }

    @field:ValidNonBlankStringList(message = "applications cannot be blank.")
    var applications: List<String> = emptyList()
        set(value) {
            val oldValue = field
            field = value
            propertyChangeSupport.firePropertyChange("applications", oldValue, value)
        }

    // 用于添加监听器
    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(listener)
    }

    // 这里的 `getState()` 返回当前状态
    override fun getState(): DeploySettingsState = this

    // `loadState()` 用于将状态加载到该类中
    override fun loadState(state: DeploySettingsState) {
        this.host = state.host
        this.username = state.username
        this.password = state.password
        this.releasePath = state.releasePath
        this.environment = state.environment
        this.projectRootPath = state.projectRootPath
        this.applications = state.applications
    }

    // 提供一个静态方法来获取服务的实例
    companion object {
        fun getInstance(project: Project): DeploySettingsState {
            return project.getService(DeploySettingsState::class.java)
        }
    }
}
