package com.cannon.entity

import javax.persistence.*

@Entity
data class Role(
        var name: String = "",

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        var users: MutableList<User> = mutableListOf(),


        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        val rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity() {
        override fun toString(): String {
                return "Role(name='$name')"
        }
}