package com.axzae.unmeta

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class UnmetaPluginTest {

    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")

        assert(project.tasks.getByName("unmetaTask") is UnmetaTask)
    }

    @Test
    fun `extension unmeta is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")

        assertNotNull(project.extensions.getByName("unmeta"))
    }

    @Test
    fun `parameters are passed correctly from extension to task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.axzae.unmeta")
        val aFile = File(project.projectDir, ".tmp")
        (project.extensions.getByName("unmeta") as UnmetaExtension).apply {
            tag.set("a-sample-tag")
            message.set("just-a-message")
            outputFile.set(aFile)
        }

        val task = project.tasks.getByName("unmetaTask") as UnmetaTask

        assertEquals("a-sample-tag", task.tag.get())
        assertEquals("just-a-message", task.message.get())
        assertEquals(aFile, task.outputFile.get().asFile)
    }
}
