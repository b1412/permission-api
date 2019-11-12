package com.github.b1412.generator.core

interface ScriptHelper {
    fun <T> exec(express: String, context: Map<String, Any>): T
}