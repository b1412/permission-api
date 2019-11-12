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

        var apiTasks: List<Task> = listOf(),

        var apiTargetPath: String,

        var uiTargetPath: String,

        var uiTasks: List<Task> = listOf(),

        var uiTemplateTargetPath: String,

        var uiTemplateTasks: List<Task> = listOf(),

        var testTargetPath: String,

        var testTasks: List<Task> = listOf()

) {
    fun generate(env: CodeEnv? = null): List<Pair<Task, List<String>>> {
        var taskes = apiTasks + uiTasks + testTasks + uiTemplateTasks
        if (env != null) {
            taskes = env.tasks
            entities = env.entities
        }
        return taskes.filter { it.active }
                //   .parallelStream()
                .map {
                    TaskService.processTask(this, it)
                }

    }
}

