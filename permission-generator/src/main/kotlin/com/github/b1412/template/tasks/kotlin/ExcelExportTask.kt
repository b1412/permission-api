package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.task.MultipleTask
import com.github.b1412.template.TaskConstants

class ExcelExportTask : MultipleTask(
        folder = { project, _ -> TaskConstants.generatedPath + TaskConstants.srcPath + project.packageName.replace(".", "/") + "/" + "excel" },
        filename = { _, entity -> entity!!.name + "ExcelParsingRule.kt" },
        templatePath = "kotlin/excelParsingRule.ftl"
)