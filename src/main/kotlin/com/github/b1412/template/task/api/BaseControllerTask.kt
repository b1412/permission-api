package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class BaseControllerTask : Task(
        active = true,
       
        name = "BaseController",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"controller/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Controller.kt" """,
        templatePath = """ "kotlin/baseController.ftl" """
)