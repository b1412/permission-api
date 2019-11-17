package com.github.b1412.generator.metadata

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EntityFeature(
        val generated: Boolean = true,

        val primaryKeyInList: Boolean = false,
        val versionInList: Boolean = false,
        val createdAtInList: Boolean = false,
        val updatedAtInList: Boolean = false,
        val creatorInList: Boolean = false,
        val modifierInList: Boolean = false,
        val userInList: Boolean = false,

        val versionInForm: Boolean = false,
        val createdAtInForm: Boolean = false,
        val updatedAtInForm: Boolean = false,
        val creatorInForm: Boolean = false,
        val modifierInForm: Boolean = false,
        val userInForm: Boolean = false,


        val version: Boolean = false,
        val security: Boolean = true,
        val tree: Boolean = false,
        val embeddedList: String = "",

        val excelImport: Boolean = false,
        val excelExport: Boolean = false,

        val entityCss: String = ""
)
