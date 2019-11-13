package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants


class DaoTask : MultipleTask(
        replaceFile = false,
        folder = { _, project, entity -> TaskConstants.apiPath + TaskConstants.srcPath + project.packageName.replace("\\.", "/") + "/" + "dao" },
        filename = { _, _, entity -> entity!!.name + "Dao.kt" },
        templatePath =  "kotlin/dao.ftl"
)