package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class DaoTask : Task(
        replaceFile = false,
       
        name = "DAO",
        folder = """ "${TaskConstants.apiPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"dao" """,
        taskType = "multiple",
        filename = """ entity.name+"Dao.kt" """,
        templatePath = """ "kotlin/dao.ftl" """
)