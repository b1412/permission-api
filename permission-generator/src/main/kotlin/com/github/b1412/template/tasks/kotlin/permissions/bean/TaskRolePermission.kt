package com.github.b1412.template.tasks.kotlin.permissions.bean

import com.github.b1412.template.tasks.kotlin.permissions.bean.TaskPermission

data class TaskRolePermission(

        var id: Long? = null,

        var version: Long? = null,

        var creatorId: Long? = null,

        var modifierId: Long? = null,

        var permissionId: Long? = null,

        var roleId: Long? = null,

        var roleName: String? = null,

        var permission: TaskPermission
)
