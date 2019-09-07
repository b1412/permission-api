package com.cannon.entity

import javax.persistence.*

@Entity
class Role(
        var name: String = "",

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        var users: MutableList<User> = mutableListOf(),


        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        val rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity()