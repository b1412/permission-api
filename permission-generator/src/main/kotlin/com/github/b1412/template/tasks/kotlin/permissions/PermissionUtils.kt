package com.github.b1412.template.tasks.kotlin.permissions


import arrow.core.extensions.list.foldable.firstOption
import arrow.core.getOrElse
import arrow.core.toOption
import com.github.b1412.generator.entity.CodePermission
import com.github.b1412.template.tasks.kotlin.permissions.bean.*
import com.google.common.base.CaseFormat

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


var conn: Connection? = null


fun getConnection() {
    val connectionProps = Properties()
    connectionProps.load(Thread.currentThread().contextClassLoader.getResourceAsStream("generator/local.properties"))
    val jdbcUser = connectionProps.getProperty("jdbcUser")
    val jdbcPassword = connectionProps.getProperty("jdbcPassword")
    val jdbcDriver = connectionProps.getProperty("jdbcDriver")
    val jdbcUrl = connectionProps.getProperty("jdbcUrl")

    connectionProps["user"] = jdbcUser
    connectionProps["password"] = jdbcPassword

    Class.forName(jdbcDriver).newInstance()
    conn = DriverManager.getConnection(jdbcUrl, connectionProps)
}

fun getResultSet(beanName: String, statement: Statement?, resultset: ResultSet?): ResultSet {
    var stmt = statement
    var res = resultset
    stmt = conn!!.createStatement()
    val sql = "SELECT * FROM $beanName;"
    res = stmt!!.executeQuery(sql)

    if (stmt.execute(sql)) {
        res = stmt.resultSet

    }
    return res
}

fun getRoles(resultset: ResultSet): List<TaskRole> {
    val roleList = mutableListOf<TaskRole>()
    while (resultset.next()) {
        val role = TaskRole()
        val roleFields = TaskRole::class.java.declaredFields
        roleFields.forEach {
            val name = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(it.name)

            val res = when (it.type) {
                java.lang.Long::class.java -> resultset.getLong(name)
                java.lang.String::class.java -> resultset.getString(name)
                else -> throw UnsupportedOperationException("${it.type}")
            }

            it.isAccessible = true
            it.set(role, res)
            it.isAccessible = false
        }
        roleList.add(role)
    }
    return roleList
}

fun getRules(resultset: ResultSet): List<TaskRule> {
    val ruleList = mutableListOf<TaskRule>()
    while (resultset.next()) {
        val rule = TaskRule()
        val ruleFileds = TaskRule::class.java.declaredFields
        ruleFileds.forEach {
            val name = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(it.name)

            val res = when (it.type) {
                java.lang.Long::class.java -> resultset.getLong(name)
                java.lang.String::class.java -> resultset.getString(name)
                else -> throw UnsupportedOperationException("${it.type}")
            }

            it.isAccessible = true
            it.set(rule, res)
            it.isAccessible = false
        }
        ruleList.add(rule)
    }
    return ruleList
}

fun getPermissions(entityName: String, baseId: Long): List<TaskPermission> {

    val permissionList = mutableListOf<TaskPermission>()
    permissionList.add(indexPermission(entityName))
    permissionList.add(createPermission(entityName))
    permissionList.add(readPermission(entityName))
    permissionList.add(updatePermission(entityName))
    permissionList.add(deletePermission(entityName))
    permissionList.add(deleteAllPermission(entityName))
    permissionList.add(excelPermission(entityName))
    permissionList.mapIndexed { index, permission ->
        permission.id = (baseId * 100 + index.inc())
    }

    return permissionList

}

fun indexPermission(entityName: String): TaskPermission {
    val permission = TaskPermission()
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    permission.version = 0
    permission.authKey = "Index $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint"
    permission.display = "Index $entityName"
    permission.entity = entityName
    permission.httpMethod = "GET"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun createPermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Create $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint"
    permission.display = "Create $entityName"
    permission.entity = entityName
    permission.httpMethod = "POST"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun readPermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Read $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint/[\\\\d]+"
    permission.display = "Read $entityName"
    permission.entity = entityName
    permission.httpMethod = "GET"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun updatePermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Update $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint/[\\\\d]+"
    permission.display = "Update $entityName"
    permission.entity = entityName
    permission.httpMethod = "PUT"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun deletePermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Delete a $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint/[\\\\d]+"
    permission.display = "Delete a $entityName"
    permission.entity = entityName
    permission.httpMethod = "DELETE"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun deleteAllPermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Delete all $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint/clear"
    permission.display = "Delete all $entityName"
    permission.entity = entityName
    permission.httpMethod = "DELETE"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun excelPermission(entityName: String): TaskPermission {
    val endPoint = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN).convert(entityName)
    val permission = TaskPermission()
    permission.version = 0
    permission.authKey = "Excel $entityName"
    permission.authUris = "/v[\\\\d]+/$endPoint/excel"
    permission.display = "Excel $entityName"
    permission.entity = entityName
    permission.httpMethod = "GET"
    permission.creatorId = 1
    permission.modifierId = 1
    return permission
}

fun getRolePermissions(taskRoleList: List<TaskRole>, permissionList: List<TaskPermission>): List<TaskRolePermission> {
    val rolePermissionList = mutableListOf<TaskRolePermission>()

    taskRoleList.map { role ->
        permissionList.map { permission ->
            val rolePermission = TaskRolePermission(
                    permission = permission,
                    version = 0,
                    creatorId = 1,
                    modifierId = 1,
                    roleName = role.name,
                    roleId = role.id,
                    permissionId = permission.id
            )

            rolePermissionList.add(rolePermission)
        }
    }

    rolePermissionList.map {
        it.id = (it.roleId.toString() + it.permissionId.toString()).toLong()
    }
    return rolePermissionList
}

/**
 * Role  Rule
 * 1 superadmin  admin 1
 * 2 appadmin    branches 5
 * 10 customer    branches
 * 100 anno         branches
 *  1 2 10 100
 *  creator 4
 */
fun getRolePermissionRule(taskRolePermissionList: List<TaskRolePermission>, taskRuleList: List<TaskRule>, permissions: List<CodePermission>): List<TaskRolePermissionRule> {

    val rolePermissionRuleList = mutableListOf<TaskRolePermissionRule>()
    val list = taskRolePermissionList
            .filter { rolePermission ->

                permissions.firstOption { it.role == rolePermission.roleName }
                        .map {
                            it.httpMethod.contains(rolePermission.permission.httpMethod)
                        }.getOrElse { true }

            }
    list.map { rolePermission ->
        val toOption = permissions.firstOption { it.role == rolePermission.roleName }
                .map {
                    val first = taskRuleList.first { r -> r.name == it.rule }
                    println("Customized Permission Setting [${rolePermission.permission.entity}]")
                    first
                }
                .getOrElse {
                    println("Default Permission Setting [${rolePermission.permission.entity}]")

                    val ruleId = when (rolePermission.roleId) {
                        1L -> taskRuleList.first { it.id== 1L } //super admin
                        2L -> taskRuleList.first { it.id == 5L }   //app admin
                        10L -> taskRuleList.first { it.id == 5L }   // customer
                        //100L -> taskRuleList.first { it.id == 4L }  //anno
                        else -> {
                            null
                        }
                    }
                    println("${rolePermission.permission.entity} ${rolePermission.roleId} ${ruleId}")
                    ruleId
                }.toOption()
        val rule = toOption.map {
                    val rolePermissionRule = TaskRolePermissionRule()
                    rolePermissionRule.rolePermissionId = rolePermission.id
                    rolePermissionRule.ruleId = it.id
                    rolePermissionRuleList.add(rolePermissionRule)
                }
        rule
    }

    return rolePermissionRuleList
}

fun getPermissionSql(permissionList: List<TaskPermission>): List<String> {
    val sqlList = mutableListOf<String>()
    permissionList.map { permission ->
        val sql = """
                INSERT INTO permission (id, version, auth_key, auth_uris, entity, http_method, creator_id, modifier_id)
                VALUES (${permission.id},${permission.version},'${permission.authKey}','${permission.authUris}','${permission.entity}','${permission.httpMethod}',${permission.creatorId}, ${permission.modifierId});
                """.trimIndent()

        sqlList.add(sql)
    }
    return sqlList
}

fun getRolePermissionSql(taskRolePermissionList: List<TaskRolePermission>): List<String> {
    val sqlList = mutableListOf<String>()
    taskRolePermissionList.map { rolePermission ->
        val sql = """
                INSERT INTO role_permission (id, version, creator_id, modifier_id, permission_id, role_id)
                VALUES (${rolePermission.id}, ${rolePermission.version}, ${rolePermission.creatorId}, ${rolePermission.modifierId}, ${rolePermission.permissionId}, ${rolePermission.roleId});
                """.trimIndent()

        sqlList.add(sql)
    }
    return sqlList
}

fun getRolePermissionRuleSql(rolePermissionRuleList: List<TaskRolePermissionRule>): List<String> {
    val sqlList = mutableListOf<String>()
    rolePermissionRuleList.map { rolePermissionRule ->
        val sql = """
                INSERT INTO role_permission_rule (role_permission_id, rule_id)
                VALUES (${rolePermissionRule.rolePermissionId}, ${rolePermissionRule.ruleId});
                """.trimIndent()

        sqlList.add(sql)
    }
    return sqlList
}
