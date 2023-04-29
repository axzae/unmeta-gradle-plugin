package com.axzae.unmeta

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import kotlin.system.measureTimeMillis

abstract class UnmetaTask : DefaultTask() {

    init {
        description = "Drop Kotlin @DebugMetadata from java classes"
        group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Option(option = "variantName", description = "Android variant (flavor + buildType)")
    abstract val variantName: Property<String>

    @get:Input
    @get:Option(option = "verbose", description = "Enable verbose logging")
    abstract val verbose: Property<Boolean>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    private val fileLogger by lazy { outputFile.get().asFile }

    @TaskAction
    fun unmetaAction() {
        if (!isEnabled) {
            log("unmeta is disabled")
            return
        }
        log("Start dropping @DebugMetadata from kotlin classes")
        val executionMs = measureTimeMillis {
            val kotlinClassesPath = project.buildDir.absolutePath + "/tmp/kotlin-classes/${variantName.get()}"
            File(kotlinClassesPath).listFiles()?.forEach { file ->
                if (file.isDirectory) removeAnnotation(file)
            }
        }
        log("Unmeta Total Time: ${executionMs}ms")
    }

    private fun log(message: String) {
        when (verbose.get()) {
            true -> logger.lifecycle(message)
            else -> logger.debug(message)
        }
        fileLogger.appendText(message + System.lineSeparator(), Charsets.UTF_8)
    }

    private fun removeAnnotation(directory: File) {
        directory.walk()
            .filter { it.path.contains("classes") && it.path.endsWith(".class") && it.isFile }
            .forEach {
                val sourceClassBytes = it.readBytes()
                val classReader = ClassReader(sourceClassBytes)
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                val unmetaClassVisitor = UnmetaClassVisitor(it.absolutePath, classWriter)
                classReader.accept(unmetaClassVisitor, ClassReader.SKIP_DEBUG)
                if (unmetaClassVisitor.isModified) {
                    log("Removed @DebugMetadata annotation from ${unmetaClassVisitor.path}")
                    it.writeBytes(classWriter.toByteArray())
                }
            }
    }
}
