package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.template.TaskConstants


class BaseControllerTask : Task(
        active = true,
        folder = { _, project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "controller/base" },
        taskType = "multiple",
        filename = { _, _, entity -> "Base" + entity!!.name + "Controller.kt" },
        templatePath = "kotlin/baseController.ftl"
)