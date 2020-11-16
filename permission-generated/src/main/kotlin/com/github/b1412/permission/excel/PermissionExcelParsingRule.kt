package com.github.b1412.permission.excel

import com.github.b1412.permission.entity.Permission
import com.github.b1412.excel.service.ExcelParsingRule
import com.github.b1412.files.parser.FileParser
import com.github.b1412.excel.convertor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class PermissionExcelParsingRule(
        @Autowired
        val entityManager: EntityManager

) : ExcelParsingRule<Permission> {

    override val fileParser: FileParser
    get() {
        val fileParser = FileParser()
        fileParser.start = 1
    fileParser.addCell(1, "entity")
    fileParser.addCell(2, "authKey")
    fileParser.addCell(3, "authUris")
    fileParser.addCell(4, "httpMethod")
    fileParser.addCell(5, "rolePermission")
    fileParser.addCell(6, "id", LongConvertor())
    fileParser.addCell(7, "version", LongConvertor())
    fileParser.addCell(8, "createdAt")
    fileParser.addCell(9, "updatedAt")
    fileParser.addCell(10, "deletedAt")
        return fileParser
    }

    override val entityClass: Class<*>
    get() = Permission::class.java

    override val ruleName: String
    get() = "permission"

    override fun process(data: List<Permission>) {
        data.forEach{
            entityManager.persist(it)
        }
    }
}
