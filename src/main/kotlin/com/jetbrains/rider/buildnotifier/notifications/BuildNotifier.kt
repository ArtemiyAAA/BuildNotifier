package com.jetbrains.rider.buildnotifier.notifications

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jetbrains.rider.buildnotifier.BuildNotifierBundle
import com.jetbrains.rider.buildnotifier.settings.AppSettingsState
import java.net.HttpURLConnection
import java.net.URI
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

object BuildNotifier {
    private val LOG = Logger.getInstance(BuildNotifier::class.java)
    private const val NOTIFICATION_GROUP_ID = "BuildNotifier.Notification"

    fun notifyBuildFinished(
        project: Project,
        buildProjectName: String,
        success: Boolean,
        durationMs: Long,
        errorCount: Int = 0
    ) {
        val settings = AppSettingsState.getInstance()

        if (settings.showBalloonNotification) {
            showBalloonNotification(project, buildProjectName, success, durationMs, errorCount)
        }

        if (settings.webhookUrl.isNotBlank()) {
            sendWebhook(buildProjectName, success, durationMs, errorCount, settings)
        }
    }

    private fun showBalloonNotification(
        project: Project,
        buildProjectName: String,
        success: Boolean,
        durationMs: Long,
        errorCount: Int
    ) {
        val durationStr = formatDuration(durationMs)
        val title = if (success) {
            BuildNotifierBundle.message("BuildNotifier.notification.build.success.title")
        } else {
            BuildNotifierBundle.message("BuildNotifier.notification.build.failed.title")
        }

        val content = if (success) {
            BuildNotifierBundle.message("BuildNotifier.notification.build.success.content", buildProjectName, durationStr)
        } else {
            BuildNotifierBundle.message("BuildNotifier.notification.build.failed.content", buildProjectName, durationStr, errorCount)
        }

        val notificationType = if (success) NotificationType.INFORMATION else NotificationType.ERROR

        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, content, notificationType)
            .notify(project)
    }

    private fun sendWebhook(
        buildProjectName: String,
        success: Boolean,
        durationMs: Long,
        errorCount: Int,
        settings: AppSettingsState
    ) {
        val webhookUrl = settings.webhookUrl

        if (webhookUrl.isBlank()) return

        CompletableFuture.runAsync {
            var connection: HttpURLConnection? = null
            try {
                val payload = renderPayload(
                    settings.webhookPayloadTemplate,
                    buildProjectName, success, durationMs, errorCount
                )

                val url = URI(webhookUrl).toURL()
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.outputStream.use { os ->
                    os.write(payload.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    val errorBody = try {
                        connection.errorStream?.bufferedReader()?.readText() ?: ""
                    } catch (_: Exception) { "" }
                    LOG.warn("Webhook failed with code $responseCode: $errorBody")
                }
            } catch (e: Exception) {
                LOG.warn("Failed to send webhook notification", e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    /**
     * Replaces template variables with actual build values.
     * Supported: ${status}, ${status_emoji}, ${project}, ${duration},
     *            ${duration_ms}, ${errors}, ${timestamp}
     */
    internal fun renderPayload(
        template: String,
        projectName: String,
        success: Boolean,
        durationMs: Long,
        errorCount: Int
    ): String {
        val status = if (success) "success" else "failed"
        val statusEmoji = if (success) "✅" else "❌"
        val durationStr = formatDuration(durationMs)
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        return template
            .replace($$"${status}", status)
            .replace($$"${status_emoji}", statusEmoji)
            .replace($$"${project}", escapeJson(projectName))
            .replace($$"${duration}", durationStr)
            .replace($$"${duration_ms}", durationMs.toString())
            .replace($$"${errors}", errorCount.toString())
            .replace($$"${timestamp}", timestamp)
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")
            append("${seconds}s")
        }.trim()
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
