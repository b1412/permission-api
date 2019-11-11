package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(ActionFeatures::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class ActionItemFeature(
        val display: String,
        val icon: String = "fa-caret-right",
        val target: String = "_self",

        val confirmTitle: String = "",
        val confirmMessage: String = "",

        val modalName: String = "",
        val modalPlaceholder: String = "",
        val modalNoteFieldName: String = "modalNote",
        val httpMethod: String,
        val url: String,
        val permissionName: String
)

/** Container annotation for repeating without repeatable support */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActionFeatures(
        vararg val value: ActionItemFeature,
        val showDelete: Boolean = true,
        val showEdit: Boolean = true
)