package com.github.b1412.template.tasks.kotlin

import com.github.b1412.template.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseServiceTask : MultipleTask(
        folder = { project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "service/base" },
        filename = {  _, entity -> "Base" + entity!!.name + "Service.kt" },
        templatePath =  "kotlin/baseService.ftl"
)