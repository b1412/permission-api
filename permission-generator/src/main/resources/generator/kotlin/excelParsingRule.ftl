package ${project.packageName}.excel

import ${project.packageName}.entity.${entity.name}
import com.github.b1412.excel.service.ExcelParsingRule
import com.github.b1412.files.parser.FileParser
import com.github.b1412.excel.convertor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class ${entity.name}ExcelParsingRule(
        @Autowired
        val entityManager: EntityManager

) : ExcelParsingRule<${entity.name}> {

    override val fileParser: FileParser
    get() {
        val fileParser = FileParser()
        fileParser.start = 1
<#list entity.fields as f>
    <#if f.type.name == "Entity">
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "${f.name?capitalize}"
                em = entityManager
        })
    <#elseif f.type.name == "Int" >
    fileParser.addCell(${f?index + 1}, "${f.name}", IntConvertor())
    <#elseif f.type.name == "Long" >
    fileParser.addCell(${f?index + 1}, "${f.name}", LongConvertor())
    <#elseif f.type.name == "Double">
    fileParser.addCell(${f?index + 1}, "${f.name}", DoubleConvertor())
    <#else>
    fileParser.addCell(${f?index + 1}, "${f.name}")
    </#if>
</#list>
        return fileParser
    }

    override val entityClass: Class<*>
    get() = ${entity.name}::class.java

    override val ruleName: String
    get() = "${entity.name?uncap_first}"

    override fun process(data: List<${entity.name}>) {
        data.forEach{
            entityManager.persist(it)
        }
    }
}
