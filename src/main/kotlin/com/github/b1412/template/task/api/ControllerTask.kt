package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.template.TaskConstants


class ControllerTask : Task(
        replaceFile = false,
        folder = { _, project, entity -> TaskConstants.apiPath + TaskConstants.srcPath +project.packageName.replace("\\.","/")+"/"+"controller"},
        taskType = "multiple",
        filename = { _, _, entity -> entity!!.name + "Controller.kt" },
        templatePath = "kotlin/controller.ftl"
)