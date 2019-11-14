package com.github.b1412.generator.entity

import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.task.Task
import com.github.b1412.generator.task.service.TaskService


data class CodeProject(
        var name: String,
        var packageName: String,

        var templateEngine: TemplateHelper,

        var entities: List<CodeEntity> = listOf(),

        var tasks: List<Task> = listOf()
) {
    fun generate(): List<Pair<Task, List<String>>> {

        return tasks
                //   .parallelStream()
                .map {
                    TaskService.processTask(this, it)
                }

    }
}

