package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(MenuFeatures::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class MenuFeature(
        val parentName: String = "",
        val name: String = "",
        val sort: Int = 0,
        val disable: Boolean = false,
        val queryParams: String = "",
        val fieldNames: Array<String> = [""],
        val columns: Array<ColumnFeature> = [],
        val actionItems: Array<String> = [],
        val toolbarItems: Array<String> = []
)

/** Container annotation for repeating without repeatable support */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class MenuFeatures(vararg val value: MenuFeature)