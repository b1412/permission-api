package com.github.leon.generator.core

abstract class TemplateHelper {

    abstract fun put(key: String, value: Any?)

    abstract fun putAll(map: MutableMap<String, Any?>)

    abstract fun exec(templateFilename: String, targetFilename: String)

}
