package com.jetbrains.rider.buildnotifier.settings

import com.intellij.openapi.options.Configurable
import com.jetbrains.rider.buildnotifier.BuildNotifierBundle
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    override fun createComponent(): JComponent {
        appSettingsComponent = AppSettingsComponent()
        return appSettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        return appSettingsComponent!!.getEnableBuildNotifications() != settings.enableBuildNotifications ||
                appSettingsComponent!!.getShowBalloonNotification() != settings.showBalloonNotification ||
                appSettingsComponent!!.getWebhookUrl() != settings.webhookUrl ||
                appSettingsComponent!!.getWebhookPayloadTemplate() != settings.webhookPayloadTemplate ||
                appSettingsComponent!!.getNotifyOnlyOnLongBuilds() != settings.notifyOnlyOnLongBuilds ||
                appSettingsComponent!!.getLongBuildThresholdMinutes() != settings.longBuildThresholdMinutes
    }

    override fun apply() {
        val settings = AppSettingsState.getInstance()
        settings.enableBuildNotifications = appSettingsComponent!!.getEnableBuildNotifications()
        settings.showBalloonNotification = appSettingsComponent!!.getShowBalloonNotification()
        settings.webhookUrl = appSettingsComponent!!.getWebhookUrl()
        settings.webhookPayloadTemplate = appSettingsComponent!!.getWebhookPayloadTemplate()
        settings.notifyOnlyOnLongBuilds = appSettingsComponent!!.getNotifyOnlyOnLongBuilds()
        settings.longBuildThresholdMinutes = appSettingsComponent!!.getLongBuildThresholdMinutes()
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        appSettingsComponent = null
    }

    override fun reset() {
        val settings = AppSettingsState.getInstance()
        appSettingsComponent!!.setEnableBuildNotifications(settings.enableBuildNotifications)
        appSettingsComponent!!.setShowBalloonNotification(settings.showBalloonNotification)
        appSettingsComponent!!.setWebhookUrl(settings.webhookUrl)
        appSettingsComponent!!.setWebhookPayloadTemplate(settings.webhookPayloadTemplate)
        appSettingsComponent!!.setNotifyOnlyOnLongBuilds(settings.notifyOnlyOnLongBuilds)
        appSettingsComponent!!.setLongBuildThresholdMinutes(settings.longBuildThresholdMinutes)
    }

    override fun getDisplayName() = BuildNotifierBundle.message("BuildNotifier.settings.title")
    override fun getPreferredFocusedComponent(): JComponent {
        return appSettingsComponent!!.getPreferredFocusedComponent()
    }
}
