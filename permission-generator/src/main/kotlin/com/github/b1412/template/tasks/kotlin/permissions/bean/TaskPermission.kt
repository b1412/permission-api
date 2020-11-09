package com.github.b1412.template.tasks.kotlin.permissions.bean

data class TaskPermission(

        var id: Long? = null,

        var version: Long? = null,

        var authKey: String? = null,

        var authUris: String? = null,

        var display: String? = null,

        var entity: String? = null,

        var httpMethod: String? = null,

        var icon: String? = null,

        var menuUrl: String? = null,

        var creatorId: Long? = null,

        var modifierId: Long? = null

)
