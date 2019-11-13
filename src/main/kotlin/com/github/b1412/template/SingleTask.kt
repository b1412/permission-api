package com.github.b1412.template

import com.github.b1412.generator.core.TemplateHelper
import com.github.b1412.generator.task.FilenameProcessor
import com.github.b1412.generator.task.Task
import com.github.b1412.generator.task.processor.SingleTaskProcessor

open class SingleTask(
        folder: FilenameProcessor,
        filename: FilenameProcessor,
        templateHelper: TemplateHelper,
        templatePath: String,
        replaceFile: Boolean = true

) : Task(
        folder = folder,
        filename = filename,
        taskType = SingleTaskProcessor(),
        templateHelper = templateHelper,
        templatePath = templatePath,
        replaceFile = replaceFile
)