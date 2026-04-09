package com.jetbrains.rider.buildnotifier.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createLifetime
import com.intellij.openapi.startup.ProjectActivity
import com.jetbrains.rider.build.BuildHost
import com.jetbrains.rider.buildnotifier.notifications.BuildNotifier
import com.jetbrains.rider.buildnotifier.settings.AppSettingsState
import com.jetbrains.rider.model.BuildResultKind

@Service(Service.Level.PROJECT)
class BuildNotificationService(private val project: Project) : Disposable {
    companion object {
        private val logger = Logger.getInstance(BuildNotificationService::class.java)

        fun getInstance(project: Project): BuildNotificationService = project.service()
    }

    private var buildStartTimeMs: Long? = null
    private var activeProjectName: String? = null

    fun initialize() {
        val buildHost = BuildHost.getInstance(project)
        val lifetime = this.createLifetime()

        buildHost.buildModel.buildSession.advise(lifetime) { session ->
            if (session != null) {
                buildStartTimeMs = System.currentTimeMillis()

                session.activeProjectName.advise(lifetime) { projectName ->
                    if (projectName.isNotBlank()) {
                        activeProjectName = projectName
                    }
                }

                session.result.advise(lifetime) { result ->
                    onBuildFinished(result.kind)
                }
            }
        }
    }

    private fun onBuildFinished(resultKind: BuildResultKind) {
        val settings = AppSettingsState.getInstance()

        if (!settings.enableBuildNotifications) return

        val startTime = buildStartTimeMs ?: System.currentTimeMillis()
        val durationMs = System.currentTimeMillis() - startTime
        buildStartTimeMs = null

        if (settings.notifyOnlyOnLongBuilds) {
            val thresholdMs = settings.longBuildThresholdMinutes * 60 * 1000L
            if (durationMs < thresholdMs) return
        }

        // Don't notify on canceled builds
        if (resultKind == BuildResultKind.Canceled) return

        val success = resultKind == BuildResultKind.Successful || resultKind == BuildResultKind.HasWarnings
        val projectName = activeProjectName ?: project.name
        
        logger.info("Build finished: $resultKind for project: $projectName")

        BuildNotifier.notifyBuildFinished(project, projectName, success, durationMs)
        activeProjectName = null
    }

    override fun dispose() {}
}

class BuildNotificationStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        BuildNotificationService.getInstance(project).initialize()
    }
}
