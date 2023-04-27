package com.axzae.unmeta

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File

abstract class UnmetaTask : DefaultTask() {

    init {
        description = "Drop Kotlin @DebugMetadata from java classes"
        group = BasePlugin.BUILD_GROUP
    }

    @TaskAction
    fun unmetaAction() {
        if (!isEnabled) {
            logger.warn("unmeta is disabled")
            return
        }

        logger.info("Start dropping @Metadata & @DebugMetadata from kotlin classes")
        project.buildDir.listFiles()?.forEach { file -> if (file.isDirectory) dropMetadata(file) }
    }

    private fun dropMetadata(directory: File) {
        directory.walk()
            .filter { it.path.contains("classes") && it.path.endsWith(".class") && it.isFile }
            .forEach {
                val sourceClassBytes = it.readBytes()
                val classReader = ClassReader(sourceClassBytes)
                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                val unmetaClassVisitor = UnmetaClassVisitor(it.absolutePath, classWriter, logger)
                classReader.accept(unmetaClassVisitor, ClassReader.SKIP_DEBUG)
                if (unmetaClassVisitor.modified) {
                    it.writeBytes(classWriter.toByteArray())
                }
            }
    }
}
