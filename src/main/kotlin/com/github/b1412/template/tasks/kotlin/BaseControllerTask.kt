package com.github.b1412.template.tasks.kotlin

import com.github.b1412.template.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseControllerTask : MultipleTask(
        folder = {  project, _ -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "controller/base" },
        filename = {  _, entity -> "Base" + entity!!.name + "Controller.kt" },
        templatePath = "kotlin/baseController.ftl"
)