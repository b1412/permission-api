package com.github.b1412.permission.excel

import com.github.b1412.permission.entity.RolePermission
import com.github.b1412.excel.service.ExcelParsingRule
import com.github.b1412.files.parser.FileParser
import com.github.b1412.excel.convertor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class RolePermissionExcelParsingRule(
        @Autowired
        val entityManager: EntityManager

) : ExcelParsingRule<RolePermission> {

    override val fileParser: FileParser
    get() {
        val fileParser = FileParser()
        fileParser.start = 1
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Role"
                em = entityManager
        })
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Permission"
                em = entityManager
        })
    fileParser.addCell(3, "rules")
    fileParser.addCell(4, "id", LongConvertor())
    fileParser.addCell(5, "version", LongConvertor())
    fileParser.addCell(6, "createdAt")
    fileParser.addCell(7, "updatedAt")
    fileParser.addCell(8, "deletedAt")
        return fileParser
    }

    override val entityClass: Class<*>
    get() = RolePermission::class.java

    override val ruleName: String
    get() = "rolePermission"

    override fun process(data: List<RolePermission>) {
        data.forEach{
            entityManager.persist(it)
        }
    }
}
