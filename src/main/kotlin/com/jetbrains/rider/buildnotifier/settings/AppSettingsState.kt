package com.jetbrains.rider.buildnotifier.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name="com.jetbrains.rider.buildnotifier.settings.AppSettingsState",
    storages= [Storage("BuildNotifierSettings.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState>, Disposable {
    companion object {
        fun getInstance() = service<AppSettingsState>()

        const val DEFAULT_WEBHOOK_PAYLOAD_TEMPLATE =
$$"""
{
  "msgtype": "text", 
  "text": "${status_emoji} Build ${status}: ${project}\nDuration: ${duration}\nTimestamp: ${timestamp}"
}"""
    }

    var enableBuildNotifications = false
    var showBalloonNotification = true
    var webhookUrl = ""
    var webhookPayloadTemplate = DEFAULT_WEBHOOK_PAYLOAD_TEMPLATE
    var notifyOnlyOnLongBuilds = true
    var longBuildThresholdMinutes = 5

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) = XmlSerializerUtil.copyBean(state, this)

    override fun dispose() {}
}
