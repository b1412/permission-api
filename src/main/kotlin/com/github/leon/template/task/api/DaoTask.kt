package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants

class DaoTask : Task(
        replaceFile = false,
        taskOfProject = TaskOfProject.API,
        name = "DAO",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"dao" """,
        taskType = "multiple",
        filename = """ entity.name+"Dao.kt" """,
        templatePath = """ "kotlin/dao.ftl" """
)