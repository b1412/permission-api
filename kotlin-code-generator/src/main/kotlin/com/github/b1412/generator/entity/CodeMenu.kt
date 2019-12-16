package com.github.b1412.generator.entity

data class CodeMenu(
        val entity: CodeEntity,
        val parentName: String,
        val name: String,
        val sort: Int = 0,
        val disable: Boolean = false,
        val fields: MutableList<CodeField> = mutableListOf(),
        val columns: MutableList<CodeColumn> = mutableListOf(),
        val actionItems: MutableList<String> = mutableListOf(),
        val toolbarItems: MutableList<String> = mutableListOf(),
        val queryParams: String
) {
    override fun toString(): String {
        return "CodeMenu(parentName='$parentName', name='$name', sort=$sort, disable=$disable, queryParams='$queryParams')"
    }
}