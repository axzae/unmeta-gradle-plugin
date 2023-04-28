package com.axzae.unmeta

import com.google.common.truth.Truth.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Ignore
import org.junit.Test

class UnmetaPluginTest {

    @Test
    @Ignore("require agp dependency")
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")

        assertThat(project.tasks.getByName("unmetaTask")).isInstanceOf(UnmetaTask::class.java)
    }

    @Test
    @Ignore("require agp dependency")
    fun `extension unmeta is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")

        assertThat(project.extensions.getByName("unmeta")).isNotNull()
    }

    @Test
    @Ignore("require agp dependency")
    fun `parameters are passed correctly from extension to task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")
        (project.extensions.getByName("unmeta") as UnmetaExtension).apply {
            isEnabled.set(false)
        }

        val task = project.tasks.getByName("unmetaTask") as UnmetaTask
        assertThat(task.isEnabled).isFalse()
    }
}
