package com.github.leon.generator.entity

data class CodeEnv(
        var tasks: List<Task> = listOf(),
        var entities: List<CodeEntity> = listOf(),
        var uiTargetPath: String = "",
        var testTargetPath: String = "",
        var uiTemplateTargetPath: String = "",
        val easyuiTargetPath: String

)