package com.stec.settings

import com.intellij.openapi.components.*
import com.stec.annotations.ValidNonBlankStringList
import jakarta.validation.constraints.NotBlank

@State(name = "DeploySettingsState", storages = [Storage("DeploySettings.xml")])
@Service
class DeploySettingsState : PersistentStateComponent<DeploySettingsState> {
    @field:NotBlank(message = "host cannot be blank.")
    var host: String = ""

    @field:NotBlank(message = "username cannot be blank.")
    var username: String = ""

    @field:NotBlank(message = "password cannot be blank.")
    var password: String = ""

    @field:NotBlank(message = "releasePath cannot be blank.")
    var releasePath: String = ""

    @field:NotBlank(message = "environment cannot be blank.")
    var environment: String = ""

    @field:NotBlank(message = "projectRootPath cannot be blank.")
    var projectRootPath: String = ""

    @field:ValidNonBlankStringList(message = "applications cannot be blank.")
    var applications: List<String> = emptyList()

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
        fun getInstance(): DeploySettingsState = service()
    }
}
