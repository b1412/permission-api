package com.github.b1412.permission.excel

import com.github.b1412.permission.entity.Rule
import com.github.b1412.excel.service.ExcelParsingRule
import com.github.b1412.files.parser.FileParser
import com.github.b1412.excel.convertor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class RuleExcelParsingRule(
        @Autowired
        val entityManager: EntityManager

) : ExcelParsingRule<Rule> {

    override val fileParser: FileParser
    get() {
        val fileParser = FileParser()
        fileParser.start = 1
    fileParser.addCell(1, "name")
    fileParser.addCell(2, "id", LongConvertor())
    fileParser.addCell(3, "version", LongConvertor())
    fileParser.addCell(4, "createdAt")
    fileParser.addCell(5, "updatedAt")
    fileParser.addCell(6, "deletedAt")
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Creator"
                em = entityManager
        })
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Modifier"
                em = entityManager
        })
        return fileParser
    }

    override val entityClass: Class<*>
    get() = Rule::class.java

    override val ruleName: String
    get() = "rule"

    override fun process(data: List<Rule>) {
        data.forEach{
            entityManager.persist(it)
        }
    }
}
