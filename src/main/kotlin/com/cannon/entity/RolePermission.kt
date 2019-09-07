package com.cannon.entity

import java.io.Serializable
import javax.persistence.*

@Entity
data class RolePermission(

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "permission_id")
        val permission: Permission? = null,


        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "role_permission_rule", joinColumns = [(JoinColumn(name = "role_permission_id"))],
                inverseJoinColumns = [(JoinColumn(name = "rule_id"))])
        val rules: MutableList<Rule> = mutableListOf()

) : BaseEntity(), Serializable {
    override fun toString(): String {
        return "RolePermission(rules=$rules)"
    }
}
