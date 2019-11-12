package com.github.b1412.template


import com.github.b1412.generator.entity.Task
import com.github.b1412.template.task.api.*


fun apiTasks(): List<Task> {
    TaskConstants.init()
    val list = listOf(BaseControllerTask(), BaseDaoTask(), BaseServiceTask(), ControllerTask(), DaoTask(), ServiceTask())
    list.forEach {
        it.targetPath = System.getProperty("user.dir")
    }
    return list

}
