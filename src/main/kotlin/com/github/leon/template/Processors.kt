package com.github.leon.template

import com.github.leon.generator.entity.CodeEntity
import com.github.leon.generator.entity.Task

val projectCache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
val entityCache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()

val excelProcessor: (Task, CodeEntity) -> Map<String, Any?> = { _, entity ->

    val headerList = entity.fields
            .filter {
                it.exportable == true
            }
            .map { """ "${it.header}" """ }

    val columnList = entity.fields
            .filter {
                it.exportable == true
            }
            .map { """ "${it.column}" """ }


    val headerListStr = "listOf(${headerList.joinToString(",")})"
    val columnListStr = "listOf(${columnList.joinToString(",")})"
    val excelFields = entity.fields.filter { it.importable!! }


    mapOf(
            "excelFields" to excelFields,
            "excelList" to headerList,
            "columnList" to columnList,
            "headerListStr" to headerListStr,
            "columnListStr" to columnListStr
    )
}

val fieldProcessor: (Task, CodeEntity) -> Map<String, Any?> = { _, entity ->
    val fields = entity.fields
    val requiredFields = fields.filter { it.required }
    mapOf(
            "requiredFields" to requiredFields
    )
}