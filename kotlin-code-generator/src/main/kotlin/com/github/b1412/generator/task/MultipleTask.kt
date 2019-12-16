package com.github.b1412.generator.task

import com.github.b1412.generator.task.processor.MultipleTaskProcessor

open class MultipleTask(
        folder: FilenameProcessor,
        filename: FilenameProcessor,
        templatePath: String,
        replaceFile: Boolean = true,
        entityExtProcessors: List<EntityExtProcessor> = listOf()
) : Task(
        folder = folder,
        filename = filename,
        taskType = MultipleTaskProcessor(),
        templatePath = templatePath,
        replaceFile = replaceFile,
        entityExtProcessors = entityExtProcessors
)