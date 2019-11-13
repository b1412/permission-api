package com.github.b1412.generator.task


import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.task.processor.ITaskProcessor


typealias FilenameProcessor = (Task, CodeProject, CodeEntity?) -> String

open class Task(
        var taskType: ITaskProcessor,

        var multiFiles: List<Map<String, Any>> = mutableListOf(),

        var targetPath: String = "",

        var folder: FilenameProcessor,

        var filename: FilenameProcessor,

        var templatePath: String,

        var replaceFile: Boolean = true,

        var templateHelper: TemplateHelper? = null
) {
    fun run(codeProject: CodeProject, root: MutableMap<String, Any>): List<String> {
        return this.taskType.run(codeProject, this, root)
    }
}

