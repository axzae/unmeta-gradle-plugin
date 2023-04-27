package com.axzae.unmeta

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "unmeta"
const val TASK_NAME = "unmetaTask"

abstract class UnmetaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, UnmetaExtension::class.java, project)

        project.tasks.register(TASK_NAME, UnmetaTask::class.java) {
            it.isEnabled = extension.isEnabled.get()
        }
    }
}
