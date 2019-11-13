package com.github.b1412.template.tasks.kotlin

import com.github.b1412.template.MultipleTask
import com.github.b1412.template.TaskConstants


class BaseDaoTask : MultipleTask(
        folder = { project, _ -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "dao/base" },
        filename = {  _, entity -> "Base" + entity!!.name + "Dao.kt" },
        templatePath = "kotlin/baseDao.ftl"
)

