package com.github.leon.generator.metadata

@Suppress("DEPRECATED_JAVA_ANNOTATION")
@java.lang.annotation.Repeatable(CssFeatures::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class CssFeature(
        val condition: String = "",
        val cssClass: String = ""
)


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CssFeatures(vararg val value: CssFeature)