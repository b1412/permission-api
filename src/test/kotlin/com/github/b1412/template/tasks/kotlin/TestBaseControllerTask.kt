package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.template.FreeMarkerHelper
import com.github.b1412.template.TaskConstants
import org.junit.jupiter.api.Test

class TestBaseControllerTask {

    @Test
    fun testBaseController(){
        val project = CodeProject(name = "test",packageName = "com.github.test",templateEngine = FreeMarkerHelper())
        val entity = CodeEntity(name = "User")
        TaskConstants.init()
        val folder = BaseControllerTask().folder.invoke(project,entity)
        val filename = BaseControllerTask().filename.invoke(project,entity)
        println(folder)
        println(filename)
    }
}