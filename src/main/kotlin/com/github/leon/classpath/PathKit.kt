package com.github.leon.classpath

import java.io.File

class PathKit {
    companion object {
        private var rootClassPath: String? = null

        fun getRootClassPath(): String {
            if (rootClassPath == null) {
                try {
                    val path = PathKit::class.java.classLoader.getResource("")!!.toURI().path
                    rootClassPath = File(path).absolutePath
                } catch (var2: Exception) {
                    val path = PathKit::class.java.classLoader.getResource("")!!.path
                    rootClassPath = File(path).absolutePath
                }

            }
            return rootClassPath!!
        }
    }
}
