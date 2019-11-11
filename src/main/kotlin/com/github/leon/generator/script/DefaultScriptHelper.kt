package com.github.leon.generator.script


import com.github.leon.generator.core.ScriptHelper
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class DefaultScriptHelper(private val language: String) : ScriptHelper {

    override fun <T> exec(express: String, context: Map<String, Any>): T {
        val factory = ScriptEngineManager()
        val scriptEngine = factory.getEngineByName(language)
        val set = context.entries
        val result: Any
        for (item in set) {
            val key = item.key
            val `val` = item.value
            scriptEngine.put(key, `val`)
        }
        try {
            result = scriptEngine.eval(express)
        } catch (e: ScriptException) {
            throw RuntimeException("exec expression[$express]fail,context [$context]", e)
        }

        return result as T
    }
}
