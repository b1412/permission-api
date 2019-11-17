package com.github.b1412.generator.entity

data class CodeToolbarItem(
        val display: String,
        val icon: String = "fa-caret-right",
        val target: String = "_self", //_blank,_self, selectOne
        val selectOneEntity: String = "",
        val multiple: Boolean = false,
        val httpMethod: String,
        val url: String,
        val permissionName: String,
        val selectOneDisplay: String,
        val confirmTitle: String = "",
        val confirmMessage: String = "",
        val modalName: String = "",
        val modalNoteFieldName: List<CodeModalField> = mutableListOf(),
        val modalPlaceholder: String = ""
)




