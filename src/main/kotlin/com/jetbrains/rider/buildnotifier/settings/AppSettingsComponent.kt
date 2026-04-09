package com.jetbrains.rider.buildnotifier.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.jetbrains.rider.buildnotifier.BuildNotifierBundle
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.ScrollPaneConstants

class AppSettingsComponent {
    private val enableBuildNotifications = JBCheckBox(BuildNotifierBundle.message("BuildNotifier.settings.enableBuildNotifications.checkbox.text")).apply {
        isSelected = AppSettingsState.getInstance().enableBuildNotifications
    }
    private val showBalloonNotification = JBCheckBox(BuildNotifierBundle.message("BuildNotifier.settings.showBalloonNotification.checkbox.text")).apply {
        isSelected = AppSettingsState.getInstance().showBalloonNotification
    }
    private val webhookUrlField = JBTextField(AppSettingsState.getInstance().webhookUrl).apply {
        columns = 40
    }
    private val webhookPayloadTemplateField = JTextArea(AppSettingsState.getInstance().webhookPayloadTemplate, 8, 50).apply {
        lineWrap = true
        wrapStyleWord = true
        font = java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12)
    }
    private val webhookPayloadScrollPane = JBScrollPane(
        webhookPayloadTemplateField,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    )
    private val notifyOnlyOnLongBuilds = JBCheckBox(BuildNotifierBundle.message("BuildNotifier.settings.notifyOnlyOnLongBuilds.checkbox.text")).apply {
        isSelected = AppSettingsState.getInstance().notifyOnlyOnLongBuilds
    }
    private val longBuildThresholdField = ComboBox(arrayOf(1, 2, 5, 10, 15, 30, 60)).apply {
        item = AppSettingsState.getInstance().longBuildThresholdMinutes
        isEditable = true
    }

    private val mainPanel: JPanel = FormBuilder
        .createFormBuilder()
        .addComponent(enableBuildNotifications, 1)
        .addComponent(showBalloonNotification, 1)
        .addComponent(notifyOnlyOnLongBuilds, 1)
        .addLabeledComponent(JBLabel(BuildNotifierBundle.message("BuildNotifier.settings.longBuildThreshold.label.text")), longBuildThresholdField, 1, false)
        .addLabeledComponent(JBLabel(BuildNotifierBundle.message("BuildNotifier.settings.webhookUrl.label.text")), webhookUrlField, 1, false)
        .addLabeledComponent(JBLabel(BuildNotifierBundle.message("BuildNotifier.settings.webhookPayloadTemplate.label.text")), webhookPayloadScrollPane, 1, true)
        .addComponent(JBLabel(BuildNotifierBundle.message("BuildNotifier.settings.webhookPayloadTemplate.hint.text")), 0)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    @Suppress("unused")
    fun getPanel() = mainPanel
    fun getPreferredFocusedComponent() = enableBuildNotifications

    fun getEnableBuildNotifications(): Boolean = enableBuildNotifications.isSelected
    fun setEnableBuildNotifications(value: Boolean) {
        enableBuildNotifications.isSelected = value
    }

    fun getShowBalloonNotification(): Boolean = showBalloonNotification.isSelected
    fun setShowBalloonNotification(value: Boolean) {
        showBalloonNotification.isSelected = value
    }

    fun getWebhookUrl(): String = webhookUrlField.text
    fun setWebhookUrl(value: String) {
        webhookUrlField.text = value
    }

    fun getWebhookPayloadTemplate(): String = webhookPayloadTemplateField.text
    fun setWebhookPayloadTemplate(value: String) {
        webhookPayloadTemplateField.text = value
    }

    fun getNotifyOnlyOnLongBuilds(): Boolean = notifyOnlyOnLongBuilds.isSelected
    fun setNotifyOnlyOnLongBuilds(value: Boolean) {
        notifyOnlyOnLongBuilds.isSelected = value
    }

    fun getLongBuildThresholdMinutes(): Int = longBuildThresholdField.editor.item as Int
    fun setLongBuildThresholdMinutes(value: Int) {
        longBuildThresholdField.editor.item = value
    }
}
