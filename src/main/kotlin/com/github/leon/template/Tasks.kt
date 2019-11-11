package com.github.leon.template


import com.github.leon.generator.entity.Task
import com.github.leon.generator.entity.TaskOfProject
import com.github.leon.generator.findClasses


fun uiTemplateTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/leon/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.UI_TEMPLATE }
    return list
}

fun apiTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/leon/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.API }
    return list

}

fun uiTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/leon/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.UI }
    return list
}

fun testTasks(): List<Task> {
    TaskConstants.init()
    val pattern = "classpath*:com/github/leon/template/task/*/*.class"
    val list = findClasses(Task::class.java, pattern)
            .map {
                (it.newInstance() as Task)
            }.filter { it.taskOfProject == TaskOfProject.TEST }
    return list
}