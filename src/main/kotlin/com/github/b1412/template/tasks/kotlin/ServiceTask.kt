package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


class ServiceTask : MultipleTask(
        replaceFile = false,
        folder = { project, _ -> TaskConstants.apiPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "service" },
        filename = { _, entity -> entity!!.name + "Service.kt" },
        templatePath = "kotlin/service.ftl"
)