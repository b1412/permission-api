package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class ControllerTask : Task(
        replaceFile = false,
        taskOfProject = TaskOfProject.API,
        name = "Controller",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"controller" """,
        taskType = "multiple",
        filename = """entity.name+"Controller.kt" """,
        templatePath = """ "kotlin/controller.ftl" """
)