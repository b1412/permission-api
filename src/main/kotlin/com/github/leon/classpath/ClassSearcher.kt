/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy start the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leon.classpath


import com.google.common.collect.Lists
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.jar.JarFile

class ClassSearcher(private val target: Class<*>) {

    val log = LoggerFactory.getLogger(ClassSearcher::class.java)!!
    private var classpath = PathKit.getRootClassPath()

    private var libDir = ""
    //private String libDir = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib";

    private var scanPackages:MutableList<String> = mutableListOf()

    private var includeAllJarsInLib = false

    private val includeJars = Lists.newArrayList<String>()

    fun <T> search(): List<Class<out T>> {
        var classFileList: MutableList<String> = Lists.newArrayList()
        if (scanPackages.isEmpty()) {
            classFileList = findFiles(classpath, "*.class")
        } else {
            for (scanPackage in scanPackages) {
                classFileList = findFiles(classpath + File.separator + scanPackage.replace("\\.".toRegex(), "\\" + File.separator), "*.class")
            }
        }
        classFileList.addAll(findjarFiles(libDir))
        return extraction<T>(target as Class<T>, classFileList)
    }

    /**
     * 查找jar包中的class
     */
    private fun findjarFiles(baseDirName: String): List<String> {
        val classFiles = Lists.newArrayList<String>()
        val baseDir = File(baseDirName)
        if (!baseDir.exists() || !baseDir.isDirectory) {
            log.error("file search error:$baseDirName is not a dir！")
        } else {
            val files = baseDir.listFiles()
            for (file in files!!) {
                if (file.isDirectory) {
                    classFiles.addAll(findjarFiles(file.absolutePath))
                } else {
                    if (includeAllJarsInLib || includeJars.contains(file.name)) {
                        var localJarFile: JarFile? = null
                        try {
                            localJarFile = JarFile(File(baseDirName + File.separator + file.name))
                            val entries = localJarFile.entries()
                            while (entries.hasMoreElements()) {
                                val jarEntry = entries.nextElement()
                                val entryName = jarEntry.name
                                println(jarEntry)
                                if (scanPackages.isEmpty()) {
                                    if (!jarEntry.isDirectory && entryName.endsWith(".class")) {
                                        val className = entryName.replace(File.separator.toRegex(), ".").substring(0, entryName.length - 6)
                                        classFiles.add(className)
                                    } else {
                                    }
                                } else {
                                    for (scanPackage in scanPackages) {
                                        val scanPackagNew = scanPackage.replace("\\.".toRegex(), "\\" + File.separator)
                                        if (!jarEntry.isDirectory && entryName.endsWith(".class") && entryName.startsWith(scanPackagNew)) {
                                            val className = entryName.replace(File.separator.toRegex(), ".").substring(0, entryName.length - 6)
                                            classFiles.add(className)
                                        }
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            try {
                                if (localJarFile != null) {
                                    localJarFile.close()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    }
                }

            }
        }
        return classFiles
    }

    fun injars(jars: List<String>?): ClassSearcher {
        if (jars != null) {
            includeJars.addAll(jars)
        }
        return this
    }

    fun inJars(vararg jars: String): ClassSearcher {
        if (jars != null) {
            for (jar in jars) {
                includeJars.add(jar)
            }
        }
        return this
    }

    fun includeAllJarsInLib(includeAllJarsInLib: Boolean): ClassSearcher {
        this.includeAllJarsInLib = includeAllJarsInLib
        return this
    }

    fun classpath(classpath: String): ClassSearcher {
        this.classpath = classpath
        return this
    }

    fun libDir(libDir: String): ClassSearcher {
        this.libDir = libDir
        return this
    }

    fun scanPackages(scanPaths: List<String>?): ClassSearcher {
        if (scanPaths != null) {
            scanPackages.addAll(scanPaths)
        }
        return this
    }

    companion object {

        private fun <T> extraction(clazz: Class<T>, classFileList: List<String>): List<Class<out T>> {
            val classList = Lists.newArrayList<Class<out T>>()
            for (classFile in classFileList) {
                var classInFile: Class<*>? = null
                try {
                    classInFile = Class.forName(classFile)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

                if (clazz.isAssignableFrom(classInFile!!) && clazz != classInFile) {
                    classList.add(classInFile as Class<out T>?)
                }
            }

            return classList
        }

        fun of(target: Class<*>): ClassSearcher {
            return ClassSearcher(target)
        }

        /**
         * @param baseDirName    查找的文件夹路径
         * @param targetFileName 需要查找的文件名
         */
        private fun findFiles(baseDirName: String, targetFileName: String): MutableList<String> {
            /**
             * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
             */
            val classFiles = Lists.newArrayList<String>()
            val baseDir = File(baseDirName)
            if (!baseDir.exists() || !baseDir.isDirectory) {
               // log.error("search error：" + baseDirName + "is not a dir！")
            } else {
                val files = baseDir.list()
                for (i in files!!.indices) {
                    val file = File(baseDirName + File.separator + files[i])
                    if (file.isDirectory) {
                        classFiles.addAll(findFiles(baseDirName + File.separator + files[i], targetFileName))
                    } else {
                        if (wildcardMatch(targetFileName, file.name)) {
                            val fileName = file.absolutePath
                            val open = PathKit.getRootClassPath() + File.separator
                            val close = ".class"
                            val start = fileName.indexOf(open)
                            val end = fileName.indexOf(close, start + open.length)
                            val className = fileName.substring(start + open.length, end).replace(File.separator.toRegex(), ".")
                            classFiles.add(className)
                        }
                    }
                }
            }
            return classFiles
        }

        /**
         * 通配符匹配
         *
         * @param pattern  通配符模式
         * @param fileName 待匹配的字符串
         */
        private fun wildcardMatch(pattern: String, fileName: String): Boolean {
            val patternLength = pattern.length
            val strLength = fileName.length
            var strIndex = 0
            var ch: Char
            for (patternIndex in 0 until patternLength) {
                ch = pattern[patternIndex]
                if (ch == '*') {
                    // 通配符星号*表示可以匹配任意多个字符
                    while (strIndex < strLength) {
                        if (wildcardMatch(pattern.substring(patternIndex + 1), fileName.substring(strIndex))) {
                            return true
                        }
                        strIndex++
                    }
                } else if (ch == '?') {
                    // 通配符问号?表示匹配任意一个字符
                    strIndex++
                    if (strIndex > strLength) {
                        // 表示str中已经没有字符匹配?了。
                        return false
                    }
                } else {
                    if (strIndex >= strLength || ch != fileName[strIndex]) {
                        return false
                    }
                    strIndex++
                }
            }
            return strIndex == strLength
        }
    }
}
