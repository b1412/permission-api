package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(ToolbarFeatures::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class ToolbarItemFeature(
        val display: String,
        val icon: String = "fa-caret-right",
        val target: String = "_self",
        val selectOneEntity: String = "",
        val selectOneDisplay: String = "",
        val multiple: Boolean = false,
        val httpMethod: String,
        val url: String,
        val permissionName: String,
        val confirmTitle: String = "",
        val confirmMessage: String = "",
        val modalName: String = "",
        val modalPlaceholder: String = "",
        val modalNoteFieldName: String = "modalNote"
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ToolbarFeatures(
        vararg val value: ToolbarItemFeature,
        val showReset: Boolean = true,
        val showAdd: Boolean = true,
        val showExcelImport: Boolean = false,
        val showExcelExport: Boolean = false
)