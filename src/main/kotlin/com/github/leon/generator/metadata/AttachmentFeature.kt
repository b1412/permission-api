package com.github.leon.generator.metadata

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class AttachmentFeature(
        val maxFiles: Int = 999
)
