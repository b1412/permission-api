package com.github.b1412.template

import java.util.Properties

object TaskConstants {
    lateinit var generatedPath: String
    lateinit var apiPath: String
    lateinit var srcPath: String
    fun init() {
        val inputStream = javaClass.classLoader.getResourceAsStream("generator/local.properties")
        val appProps = Properties()
        appProps.load(inputStream)
        val projectName = appProps.getProperty("projectName")
        generatedPath = "/$projectName-generated"
        apiPath = "/$projectName-api"
        srcPath = "/src/main/kotlin/"
    }
}
