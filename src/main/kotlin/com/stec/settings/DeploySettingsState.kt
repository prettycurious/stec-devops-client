package com.stec.settings

import com.intellij.openapi.components.*

@State(name = "DeploySettingsState", storages = [Storage("DeploySettings.xml")])
@Service
class DeploySettingsState : PersistentStateComponent<DeploySettingsState> {
    var host: String = ""
    var username: String = ""
    var password: String = ""
    var releasePath: String = ""
    var environment: String = ""
    var projectRootPath: String = ""
    var applications: List<String> = listOf("")

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
