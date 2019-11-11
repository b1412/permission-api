package com.github.leon.generator.task.processor

import com.github.leon.generator.entity.CodeProject
import com.github.leon.generator.entity.Task
import com.github.leon.generator.task.service.TaskService
import com.google.common.collect.Lists

class SingleTaskProcessor : ITaskProcessor {
    override fun run(codeProject: CodeProject, task: Task, context: MutableMap<String, Any>): List<String> {
        return TaskService.processTemplate(codeProject, task, context)

    }
}
