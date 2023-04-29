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

    private val fileLogger by lazy {
        outputFile.get().asFile.apply {
            writeText("", Charsets.UTF_8)
        }
    }

    private var scannedFiles = 0
    private var modifiedFiles = 0

    @TaskAction
    fun unmetaAction() {
        if (!isEnabled) {
            log("unmeta is disabled")
            return
        }
        log("Start dropping @DebugMetadata from kotlin classes")
        val executionMs = measureTimeMillis {
            val kotlinClassesBasePathName = project.buildDir.absolutePath + "/tmp/kotlin-classes/${variantName.get()}"
            val kotlinClassesBasePath = File(kotlinClassesBasePathName)
            kotlinClassesBasePath.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { directory -> removeAnnotation(directory, kotlinClassesBasePath) }
        }
        log(
            listOf(
                "Task finished.",
                "Class files scanned: $scannedFiles",
                "Class files modified: $modifiedFiles",
                "Execution time: ${executionMs}ms.",
            ).joinToString("\n"),
        )
    }

    private fun log(message: String) {
        when (verbose.get()) {
            true -> logger.lifecycle(message)
            else -> logger.debug(message)
        }
        fileLogger.appendText(message + System.lineSeparator(), Charsets.UTF_8)
    }

    private fun removeAnnotation(directory: File, basePath: File) {
        directory.walk()
            .filter { it.path.contains("classes") && it.path.endsWith(".class") && it.isFile }
            .forEach {
                ++scannedFiles
                val sourceClassBytes = it.readBytes()
                val classReader = ClassReader(sourceClassBytes)
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                val unmetaClassVisitor = UnmetaClassVisitor(classWriter)
                classReader.accept(unmetaClassVisitor, ClassReader.SKIP_DEBUG)
                if (unmetaClassVisitor.isModified) {
                    ++modifiedFiles
                    log("- Removed @DebugMetadata annotation from ${it.toRelativeString(basePath)}")
                    it.writeBytes(classWriter.toByteArray())
                }
            }
    }
}
