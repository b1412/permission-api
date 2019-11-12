package com.github.b1412.template


import com.github.b1412.generator.findClasses
import com.github.b1412.generator.entity.Task
import com.github.b1412.generator.entity.TaskOfProject
import com.github.b1412.generator.findClasses


fun uiTemplateTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/b1412/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.UI_TEMPLATE }
    return list
}

fun apiTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/b1412/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.API }
    return list

}

fun uiTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/b1412/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.UI }
    return list
}

