package com.github.leon.generator.entity

data class CodeActionItem(
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




