package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(ColumnFeatures::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class ColumnFeature(
        val menuName: String = "",
        val isAsc: Boolean = false,
        val columnDisplay: String = "",
        val columnModel: String = "",
        val isSortable: Boolean = true,
        val isActive: Boolean = false,
        val display: Boolean = true,
        val hiddenInList: Boolean = false,
        val hiddenInForm: Boolean = false
)

/** Container annotation for repeating without repeatable support */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ColumnFeatures(vararg val value: ColumnFeature)