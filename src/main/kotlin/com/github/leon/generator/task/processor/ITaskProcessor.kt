package com.github.leon.generator.task.processor


import com.github.leon.generator.entity.CodeProject
import com.github.leon.generator.entity.Task

interface ITaskProcessor {
    fun run(codeProject: CodeProject, task: Task, context: MutableMap<String, Any>): List<String>
}
