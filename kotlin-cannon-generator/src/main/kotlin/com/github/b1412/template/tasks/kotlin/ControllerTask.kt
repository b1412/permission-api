package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.ext.Utils
import com.github.b1412.generator.task.Task
import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


val entityNames: (Task, CodeEntity) -> Map<String, Any> = { _, entity ->
    val map = mutableMapOf(
            "lowerHyphenName" to Utils.lowerHyphen(entity.name)
    )
    map
}


class ControllerTask : MultipleTask(
        replaceFile = false,
        folder = { project, _ -> TaskConstants.apiPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "controller" },
        filename = { _, entity -> entity!!.name + "Controller.kt" },
        templatePath = "kotlin/controller.ftl",
        entityExtProcessors = listOf(entityNames)

)