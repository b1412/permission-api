package com.github.b1412.generator.entity

data class CodeAction(
        val showDelete: Boolean = true,
        val showEdit: Boolean = true,
        val codeActionItems: List<CodeActionItem> = mutableListOf()
)




