package com.axzae.unmeta

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

const val DEFAULT_IS_ENABLED = true

@Suppress("UnnecessaryAbstractClass")
abstract class UnmetaExtension @Inject constructor(project: Project) {

    private val objects = project.objects

    val isEnabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(DEFAULT_IS_ENABLED)
}
