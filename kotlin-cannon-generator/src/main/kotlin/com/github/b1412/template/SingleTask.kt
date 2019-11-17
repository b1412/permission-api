package com.github.b1412.template

import com.github.b1412.generator.task.FilenameProcessor
import com.github.b1412.generator.task.Task
import com.github.b1412.generator.task.processor.SingleTaskProcessor

open class SingleTask(
        folder: FilenameProcessor,
        filename: FilenameProcessor,
        templatePath: String,
        replaceFile: Boolean = true

) : Task(
        folder = folder,
        filename = filename,
        taskType = SingleTaskProcessor(),
        templatePath = templatePath,
        replaceFile = replaceFile
)