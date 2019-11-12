package com.github.b1412.generator.task.processor

import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.task.service.TaskService
import com.google.common.collect.Lists

class SingleTaskProcessor : ITaskProcessor {
    override fun run(codeProject: CodeProject, task: Task, context: MutableMap<String, Any>): List<String> {
        return TaskService.processTemplate(codeProject, task, context)

    }
}
