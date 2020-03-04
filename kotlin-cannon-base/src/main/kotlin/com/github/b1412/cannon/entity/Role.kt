package com.github.b1412.cannon.entity

import java.io.Serializable
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
data class Role(
        var name: String = "",

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        var users: MutableList<User> = mutableListOf(),


        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "role", fetch = FetchType.LAZY, orphanRemoval = true)
        val rolePermissions: MutableList<RolePermission> = mutableListOf()

) : BaseEntity(), Serializable {
    override fun toString(): String {
        return "Role(name='$name')"
    }
}