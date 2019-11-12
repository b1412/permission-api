package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class BaseServiceTask : Task(
        taskOfProject = TaskOfProject.API,
        name = "baseService",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"service/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Service.kt" """,
        templatePath = """ "kotlin/baseService.ftl" """
)