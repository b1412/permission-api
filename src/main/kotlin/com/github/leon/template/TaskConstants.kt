package com.github.leon.template

import java.util.*

object TaskConstants {
    lateinit var generatedPath: String
    lateinit var apiPath: String
    lateinit var srcPath: String
    fun init() {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream("generator/local.properties")
        val appProps = Properties()
        appProps.load(inputStream)
        val projectName = appProps.getProperty("projectName")
        generatedPath = "/$projectName-generated"
        apiPath = "/$projectName-api"
        srcPath = "/src/main/kotlin/"
    }
}