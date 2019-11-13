package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseControllerTask : MultipleTask(
        folder = { _, project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "controller/base" },
        filename = { _, _, entity -> "Base" + entity!!.name + "Controller.kt" },
        templatePath = "kotlin/baseController.ftl"
)