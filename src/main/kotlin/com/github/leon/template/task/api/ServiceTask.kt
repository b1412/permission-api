package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants

class ServiceTask : Task(
        replaceFile = false,
        taskOfProject = TaskOfProject.API,
        name = "Service",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"service" """,
        taskType = "multiple",
        filename = """ entity.name+"Service.kt" """,
        templatePath = """ "kotlin/service.ftl" """
)