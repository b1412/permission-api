package com.github.leon.generator.metadata

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelFeature(
        val importable: Boolean = false,
        val exportable: Boolean = false,
        val column: String = "",
        val header: String = ""
)
