package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants
import com.github.leon.template.task.ngxProcessor

class ControllerTask : Task(
        replaceFile = false,
        taskOfProject = TaskOfProject.API,
        name = "Controller",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"controller" """,
        taskType = "multiple",
        filename = """entity.name+"Controller.kt" """,
        templatePath = """ "kotlin/controller.ftl" """,
        entityExtProcessor = ngxProcessor
)