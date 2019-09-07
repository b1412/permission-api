package com.cannon.entity

import javax.persistence.*

@Entity
class Role(
        var name: String = "",

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
        var users: MutableList<User> = mutableListOf(),


        @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        @JoinColumn(name = "role_id")
        val rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity()