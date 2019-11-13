package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseServiceTask : MultipleTask(
        folder = { _, project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "service/base" },
        filename = { _, _, entity -> "Base" + entity!!.name + "Service.kt" },
        templatePath =  "kotlin/baseService.ftl"
)