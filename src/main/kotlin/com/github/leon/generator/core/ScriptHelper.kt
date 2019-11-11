package com.github.leon.generator.core

interface ScriptHelper {
    fun <T> exec(express: String, context: Map<String, Any>): T
}