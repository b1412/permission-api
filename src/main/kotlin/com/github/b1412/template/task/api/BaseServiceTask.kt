package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.template.TaskConstants


class BaseServiceTask : Task(
        folder = { _, project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "service/base" },
        taskType = "multiple",
        filename = { _, _, entity -> "Base" + entity!!.name + "Service.kt" },
        templatePath =  "kotlin/baseService.ftl"
)