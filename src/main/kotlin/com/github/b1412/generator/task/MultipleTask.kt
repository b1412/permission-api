package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.MultipleTaskProcessor

open class MultipleTask(
        folder: FilenameProcessor,
        filename: FilenameProcessor,
        templatePath: String,
        replaceFile: Boolean = true
) : Task(
        folder = folder,
        filename = filename,
        taskType = MultipleTaskProcessor(),
        templatePath = templatePath,
        replaceFile = replaceFile
)