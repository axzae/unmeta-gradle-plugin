package com.axzae.unmeta

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

const val DEFAULT_IS_ENABLED = true
const val DEFAULT_VERBOSE = false
const val DEFAULT_OUTPUT_FILE = "outputs/logs/unmeta-report.txt"

@Suppress("UnnecessaryAbstractClass")
abstract class UnmetaExtension @Inject constructor(project: Project) {

    private val objects = project.objects

    val isEnabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(DEFAULT_IS_ENABLED)

    val verbose: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(DEFAULT_VERBOSE)

    val outputFile: RegularFileProperty = objects.fileProperty()
        .convention(project.layout.buildDirectory.file(DEFAULT_OUTPUT_FILE))
}
