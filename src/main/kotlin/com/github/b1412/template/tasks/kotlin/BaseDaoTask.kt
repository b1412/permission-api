package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseDaoTask : MultipleTask(
        folder = { _, project, entity -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "dao/base" },
        filename = { _, _, entity -> "Base" + entity!!.name + "Dao.kt" },
        templatePath = "kotlin/baseDao.ftl"
)

