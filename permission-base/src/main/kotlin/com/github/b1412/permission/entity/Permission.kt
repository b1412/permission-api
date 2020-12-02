package com.github.b1412.permission.entity

import com.github.b1412.api.entity.BaseEntity
import java.io.Serializable
import javax.persistence.*

@Entity
data class Permission(
    var entity: String? = null,

    var authKey: String? = null,

    var authUris: String? = null,

    var httpMethod: String? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "permission_id")
    var rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity(), Serializable
