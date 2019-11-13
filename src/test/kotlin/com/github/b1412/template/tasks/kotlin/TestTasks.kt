package com.github.b1412.template.tasks.kotlin

import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.template.FreeMarkerHelper
import com.github.b1412.template.TaskConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestTasks {

    val project = CodeProject(name = "test", packageName = "com.github.test", templateEngine = FreeMarkerHelper())
    val entity = CodeEntity(name = "User")

    @BeforeEach
    fun init() {
        TaskConstants.init()
    }

    @Test
    fun testBaseController() {
        val folder = BaseControllerTask().folder.invoke(project, entity)
        val filename = BaseControllerTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-generated/src/main/kotlin/com/github/test/controller/base")
        assertThat(filename).isEqualTo("BaseUserController.kt")
    }

    @Test
    fun testBaseDao() {
        val folder = BaseDaoTask().folder.invoke(project, entity)
        val filename = BaseDaoTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-generated/src/main/kotlin/com/github/test/dao/base")
        assertThat(filename).isEqualTo("BaseUserDao.kt")
    }


    @Test
    fun testBaseService() {
        val folder = BaseServiceTask().folder.invoke(project, entity)
        val filename = BaseServiceTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-generated/src/main/kotlin/com/github/test/service/base")
        assertThat(filename).isEqualTo("BaseUserService.kt")
    }

    @Test
    fun testController() {
        val folder = ControllerTask().folder.invoke(project, entity)
        val filename = ControllerTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-api/src/main/kotlin/com/github/test/controller")
        assertThat(filename).isEqualTo("UserController.kt")
    }

    @Test
    fun testDao() {
        val folder = DaoTask().folder.invoke(project, entity)
        val filename = DaoTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-api/src/main/kotlin/com/github/test/dao")
        assertThat(filename).isEqualTo("UserDao.kt")
    }


    @Test
    fun testService() {
        val folder = ServiceTask().folder.invoke(project, entity)
        val filename = ServiceTask().filename.invoke(project, entity)
        assertThat(folder).isEqualTo("/testcannon-api/src/main/kotlin/com/github/test/service")
        assertThat(filename).isEqualTo("UserService.kt")
    }

}