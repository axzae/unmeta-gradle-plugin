package com.axzae.unmeta

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "unmeta"
const val TASK_NAME = "unmetaTask"

abstract class UnmetaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'template' extension object
        val extension = project.extensions.create(EXTENSION_NAME, UnmetaExtension::class.java, project)

        // Add a task that uses configuration from the extension object
        project.tasks.register(TASK_NAME, UnmetaTask::class.java) {
            it.tag.set(extension.tag)
            it.message.set(extension.message)
            it.outputFile.set(extension.outputFile)
        }
    }
}
