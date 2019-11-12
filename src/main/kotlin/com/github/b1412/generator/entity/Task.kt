package com.github.b1412.generator.entity


import com.github.b1412.generator.core.ScriptHelper
import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.task.processor.ITaskProcessor
import org.joor.Reflect

typealias ProjectExtProcessor = (Task, CodeProject) -> Map<String, Any?>

typealias EntityExtProcessor = (Task, CodeEntity) -> Map<String, Any?>

open class Task(
        var id: Int? = null,

        var name: String = "",

        var taskType: String = "",

        var multiFiles: List<Map<String, Any>> = mutableListOf(),

        var taskOfProject: TaskOfProject = TaskOfProject.API,

        var folder: String = "",

        var filename: String = "",

        var templatePath: String = "",

        var replaceFile: Boolean = true,

        var active: Boolean = true,

        var scriptHelper: ScriptHelper? = null,

        var templateHelper: TemplateHelper? = null,

        var projectExtProcessor: ProjectExtProcessor? = null,

        var entityExtProcessor: EntityExtProcessor? = null
) {

    fun run(codeProject: CodeProject, root: MutableMap<String, Any>): List<String> {
        return taskProcessor(taskType).run(codeProject, this, root)
    }

    private fun taskProcessor(taskType: String): ITaskProcessor {
        return Reflect.on("${ITaskProcessor::class.java.`package`.name}.${taskType.capitalize()}TaskProcessor").create().get()
    }

}

