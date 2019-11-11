package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants

class BaseServiceTask : Task(
        taskOfProject = TaskOfProject.API,
        name = "baseService",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"service/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Service.kt" """,
        templatePath = """ "kotlin/baseService.ftl" """
)