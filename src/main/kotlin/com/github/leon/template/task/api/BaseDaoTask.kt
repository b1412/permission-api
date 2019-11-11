package com.github.leon.template.task.api

import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.template.TaskConstants

class BaseDaoTask : Task(
        taskOfProject = TaskOfProject.API,
        name = "BaseDao",
        folder = """ "${TaskConstants.generatedPath}"+"${TaskConstants.srcPath}"+project.packageName.replaceAll("\\.","/")+"/"+"dao/base" """,
        taskType = "multiple",
        filename = """ "Base"+entity.name+"Dao.kt" """,
        templatePath = """ "kotlin/baseDao.ftl" """
)