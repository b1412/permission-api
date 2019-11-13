package com.github.b1412.generator.task.service

import com.github.b1412.generator.entity.CodeEntity
import com.github.b1412.generator.entity.CodeProject
import com.github.b1412.generator.entity.Task
import com.google.common.collect.Maps
import freemarker.ext.beans.BeansWrapper
import freemarker.template.TemplateHashModel
import org.apache.commons.beanutils.PropertyUtils
import java.io.File


object TaskService {

    fun processTask(codeProject: CodeProject, task: Task): Pair<Task, List<String>> {
        val paths: List<String>
        val scope = Maps.newHashMap<String, Any>()
        val codeProjectMap = PropertyUtils.describe(codeProject)
        //  codeProjectMap.putAll(task.projectExtProcessor!!.invoke(task, codeProject))
        scope["project"] = codeProjectMap
        scope["enums"] = codeProject.enums

        codeProject.utilClasses.forEach {
            val wrapper = BeansWrapper.getDefaultInstance()
            val staticModels = wrapper.staticModels
            val fileStatics = staticModels.get(it.name) as TemplateHashModel
            scope[it.simpleName] = fileStatics
        }
        task.templateHelper = codeProject.templateEngine
        task.templateHelper!!.putAll(scope)
        paths = task.run(codeProject, scope)
        return Pair(task, paths)
    }

    fun processTemplate(codeProject: CodeProject, codeEntity: CodeEntity?, task: Task, root: Map<String, Any>): List<String> {
        /*    List<TaskParam> params = task.getTaskParams();
        for (TaskParam param : params) {
            if (StrKit.isBlank(param.getStr("name"))) continue;
            String value = param.getStr("expression");
            Config.templateEngine().put(param.getStr("name"), Config.scriptHelper().exec(value, root));
        }*/

        if (task.multiFiles.isEmpty()) {
            val templateFilename = task.templatePath
            var folder = task.folder(task, codeProject, codeEntity)
            folder = task.targetPath + File.separator + folder

            val folderDir = File(folder)
            if (!folderDir.exists()) {
                folderDir.mkdirs()
            }
            // val filename = codeProject.scriptHelper.exec<Any>(task.filename, root).toString()
            val filename = task.filename(task, codeProject, codeEntity)
            val outputFilename = folder + File.separator + filename
            val outputFile = File(outputFilename)
            if (task.replaceFile || !outputFile.exists()) {
                task.templateHelper!!.exec(templateFilename, outputFilename)
            }
            return listOf(outputFilename)
        } else {
            return task.multiFiles.map {
                val newRoot = root + it
                it.forEach { e ->
                    task.templateHelper!!.put(e.key, e.value)
                }
                val templateFilename = task.templatePath
                var folder = task.folder(task, codeProject, codeEntity)
                folder = task.targetPath + File.separator + folder
                val folderDir = File(folder)
                if (!folderDir.exists()) {
                    folderDir.mkdirs()
                }
                // val filename = codeProject.scriptHelper.exec<Any>(task.filename(ta), newRoot).toString()
                val filename = task.filename(task, codeProject, codeEntity)
                val outputFilename = folder + File.separator + filename
                val outputFile = File(outputFilename)
                if (task.replaceFile || !outputFile.exists()) {
                    task.templateHelper!!.exec(templateFilename, outputFilename)
                }
                outputFilename
            }

        }


    }


}
