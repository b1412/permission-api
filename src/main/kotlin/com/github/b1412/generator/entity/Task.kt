package com.github.b1412.generator.entity


import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.task.processor.ITaskProcessor
import org.joor.Reflect


typealias FilenameProcessor = (Task, CodeProject, CodeEntity?) -> String

open class Task(
        var taskType: String = "",

        var multiFiles: List<Map<String, Any>> = mutableListOf(),

        var targetPath: String = "",

        var folder: FilenameProcessor ,

        var filename: FilenameProcessor,

        var templatePath: String = "",

        var replaceFile: Boolean = true,

        var active: Boolean = true,

        var templateHelper: TemplateHelper? = null
) {

    fun run(codeProject: CodeProject, root: MutableMap<String, Any>): List<String> {
        return taskProcessor(taskType).run(codeProject, this, root)
    }

    private fun taskProcessor(taskType: String): ITaskProcessor {
        return Reflect.on("${ITaskProcessor::class.java.`package`.name}.${taskType.capitalize()}TaskProcessor").create().get()
    }

}

