package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION") // suppress deprecation for java.lang.annotation.Repeatable
@java.lang.annotation.Repeatable(PermissionFeatures::class) // define container for compatibility
// standard annotation definition follows:
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable // use Kotlin annotation as well just to be safe (for tools and stuff)
annotation class PermissionFeature(
        val role: String,
        val rule: String,
        val httpMethods: Array<String> = ["GET", "POST", "DELETE", "PUT"]
)

/** Container annotation for repeating without repeatable support */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class PermissionFeatures(vararg val value: PermissionFeature)