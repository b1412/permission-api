package com.github.leon.generator.entity

data class CodeColumn(
        val asc: Boolean = false,
        val columnDisplay: String = "Version",
        val columnModel: String = "version",
        val sortable: Boolean = true,
        val active: Boolean = false,
        val display: Boolean = true,
        val hiddenInList: Boolean = true,
        val hiddenInForm: Boolean = true
)




