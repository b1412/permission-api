package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class ServiceTask : Task(
        replaceFile = false,
        taskOfProject = TaskOfProject.API,
        name = "Service",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"service" """,
        taskType = "multiple",
        filename = """ entity.name+"Service.kt" """,
        templatePath = """ "kotlin/service.ftl" """
)