package com.github.b1412.template.tasks.kotlin

import com.github.b1412.template.MultipleTask
import com.github.b1412.template.TaskConstants


class ControllerTask : MultipleTask(
        replaceFile = false,
        folder = { project, _ -> TaskConstants.apiPath + TaskConstants.srcPath +project.packageName.replace(".","/")+"/"+"controller"},
        filename = {  _, entity -> entity!!.name + "Controller.kt" },
        templatePath = "kotlin/controller.ftl"
)