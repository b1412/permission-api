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
    fun generate() {
        tasks.parallelStream().forEach {
            TaskService.processTask(this, it)
        }
    }
}

