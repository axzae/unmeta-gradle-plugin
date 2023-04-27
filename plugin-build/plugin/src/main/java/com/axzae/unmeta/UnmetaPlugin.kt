package com.axzae.unmeta

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

abstract class UnmetaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("unmeta", UnmetaExtension::class.java, project)
        val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { variant ->
            val compileKotlinTaskName = "compile${variant.name.capitalized()}Kotlin"
            val unmetaTask = project.tasks.create("unmeta${variant.name.capitalized()}", UnmetaTask::class.java).apply {
                isEnabled = extension.isEnabled.get()
                verbose.set(extension.verbose.get())
                variantName.set(variant.name)
                outputFile.set(extension.outputFile)
            }
            project.afterEvaluate {
                project.tasks.findByName(compileKotlinTaskName)?.finalizedBy(unmetaTask)
            }
        }
    }
}
