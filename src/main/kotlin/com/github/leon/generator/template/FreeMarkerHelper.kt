package com.github.leon.generator.template


import com.github.leon.generator.core.TemplateHelper
import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import freemarker.template.SimpleHash
import java.io.*


class FreeMarkerHelper(templatesBaseDir: String? = null) : TemplateHelper() {
    protected var configuration: Configuration = Configuration(Configuration.VERSION_2_3_29)
    protected var context: SimpleHash = SimpleHash()

    init {
        if (templatesBaseDir != null) {
            val loader = FileTemplateLoader(File(templatesBaseDir))
            configuration.templateLoader = loader
        } else {
            configuration.setClassForTemplateLoading(FreeMarkerHelper::class.java, "/generator")
        }
    }

    override fun put(key: String, value: Any?) {
        var value = value
        if (value is Map<*, *>) {
            value = SimpleHash(value)
        }
        context.put(key, value)
    }

    override fun putAll(map: MutableMap<String, Any?>) {
        for (key in map.keys) {
            put(key, map[key] as Any)
        }
    }

    override fun exec(templateFilename: String, targetFilename: String) {
        var out: Writer? = null
        try {
            out = BufferedWriter(OutputStreamWriter(FileOutputStream(targetFilename), "UTF-8"))
            val template = configuration.getTemplate(templateFilename, "UTF-8")
            template.process(context, out)
        } catch (e: Exception) {
            throw RuntimeException("parse template file [$templateFilename] error", e)
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }
        }
    }


}
