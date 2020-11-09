package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.ext.Utils
import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants

class PermissionTask : MultipleTask(
        folder = { _, entity -> """${TaskConstants.generatedPath}/db/${Utils.lowerHyphen(entity!!.name)}/""" },
        filename = { _, entity -> Utils.lowerHyphen(entity!!.name) + "-permission.sql" },
        templatePath = "kotlin/permission.ftl",
        entityExtProcessors = listOf(entityPermissionProcessor),
        ignoreEntities = builtInEntities
)
