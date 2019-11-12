package com.github.b1412.template.task.api

import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.template.TaskConstants


class BaseDaoTask : Task(
        taskOfProject = TaskOfProject.API,
        name = "BaseDao",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"dao/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Dao.kt" """,
        templatePath = """ "kotlin/baseDao.ftl" """
)