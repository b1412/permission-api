package com.github.b1412.cannon.entity


import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
data class Permission(

        var entity: String? = null,

        var authKey: String? = null,

        var display: String? = null,

        var httpMethod: String? = null,

        var authUris: String? = null,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "permission")
        var rolePermission: MutableList<RolePermission> = mutableListOf()

) : BaseEntity(), Serializable
