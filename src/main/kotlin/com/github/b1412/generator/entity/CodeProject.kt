package com.github.b1412.generator.entity

import com.github.b1412.generator.core.ScriptHelper
import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.task.service.TaskService


data class CodeProject(
        var name: String,
        var packageName: String,

        var scriptHelper: ScriptHelper,

        var templateEngine: TemplateHelper,

        var entities: List<CodeEntity> = listOf(),

        val enums: List<CodeEnum> = listOf(),

        var utilClasses: List<Class<*>> = listOf(),

        var tasks: List<Task> = listOf()
) {
    fun generate(): List<Pair<Task, List<String>>> {

        return tasks.filter { it.active }
                //   .parallelStream()
                .map {
                    TaskService.processTask(this, it)
                }

    }
}

