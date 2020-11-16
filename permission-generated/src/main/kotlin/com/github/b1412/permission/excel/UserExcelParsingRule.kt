package com.github.b1412.permission.excel

import com.github.b1412.permission.entity.User
import com.github.b1412.excel.service.ExcelParsingRule
import com.github.b1412.files.parser.FileParser
import com.github.b1412.excel.convertor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class UserExcelParsingRule(
        @Autowired
        val entityManager: EntityManager

) : ExcelParsingRule<User> {

    override val fileParser: FileParser
    get() {
        val fileParser = FileParser()
        fileParser.start = 1
    fileParser.addCell(1, "login")
    fileParser.addCell(2, "firstname")
    fileParser.addCell(3, "lastname")
    fileParser.addCell(4, "address")
    fileParser.addCell(5, "email")
    fileParser.addCell(6, "notes")
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Branch"
                em = entityManager
        })
        fileParser.addCell(2, "category", EntityConvertor().apply {
                name = "Role"
                em = entityManager
        })
    fileParser.addCell(9, "clientId")
    fileParser.addCell(10, "expiresIn", LongConvertor())
    fileParser.addCell(11, "active")
    fileParser.addCell(12, "username")
    fileParser.addCell(13, "password")
    fileParser.addCell(14, "confirmPassword")
    fileParser.addCell(15, "grantedAuthorities")
    fileParser.addCell(16, "id", LongConvertor())
    fileParser.addCell(17, "version", LongConvertor())
    fileParser.addCell(18, "createdAt")
    fileParser.addCell(19, "updatedAt")
    fileParser.addCell(20, "deletedAt")
        return fileParser
    }

    override val entityClass: Class<*>
    get() = User::class.java

    override val ruleName: String
    get() = "user"

    override fun process(data: List<User>) {
        data.forEach{
            entityManager.persist(it)
        }
    }
}
