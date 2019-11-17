package com.github.b1412.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(UserColumns::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class UserColumn(
        val name: String = "",
        val columnFeatures: Array<ColumnFeature>
)

/** Container annotation for repeating without repeatable support */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class UserColumns(vararg val value: UserColumn)