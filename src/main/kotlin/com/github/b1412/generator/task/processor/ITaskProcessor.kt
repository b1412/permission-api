package com.github.b1412.generator.task.processor


import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.entity.Task

interface ITaskProcessor {
    fun run(codeProject: CodeProject, task: Task, context: MutableMap<String, Any>): List<String>
}
