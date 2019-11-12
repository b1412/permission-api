package com.github.b1412.classpath

import java.io.File

class PathKit {
    companion object {
        private var rootClassPath: String? = null

        fun getRootClassPath(): String {
            if (com.github.b1412.classpath.PathKit.Companion.rootClassPath == null) {
                try {
                    val path = com.github.b1412.classpath.PathKit::class.java.classLoader.getResource("")!!.toURI().path
                    com.github.b1412.classpath.PathKit.Companion.rootClassPath = File(path).absolutePath
                } catch (var2: Exception) {
                    val path = com.github.b1412.classpath.PathKit::class.java.classLoader.getResource("")!!.path
                    com.github.b1412.classpath.PathKit.Companion.rootClassPath = File(path).absolutePath
                }

            }
            return com.github.b1412.classpath.PathKit.Companion.rootClassPath!!
        }
    }
}
