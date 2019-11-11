package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants
import com.github.leon.template.excelProcessor

class BaseControllerTask : Task(
        active = true,
        taskOfProject = TaskOfProject.API,
        name = "BaseController",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"controller/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Controller.kt" """,
        templatePath = """ "kotlin/baseController.ftl" """,
        entityExtProcessor = excelProcessor
)