package com.github.leon.generator.entity

data class CodeAction(
        val showDelete: Boolean = true,
        val showEdit: Boolean = true,
        val codeActionItems: List<CodeActionItem> = mutableListOf()
)




