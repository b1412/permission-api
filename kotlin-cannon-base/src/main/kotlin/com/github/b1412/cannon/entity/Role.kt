package com.github.b1412.cannon.entity

import java.io.Serializable
import javax.persistence.*

@Entity
data class Role(
        var name: String = "",

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        var users: MutableList<User> = mutableListOf(),


        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        val rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity(),Serializable {
        override fun toString(): String {
                return "Role(name='$name')"
        }
}