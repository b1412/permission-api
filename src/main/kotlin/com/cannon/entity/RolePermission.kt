package com.cannon.entity

import javax.persistence.*

@Entity
data class RolePermission(

        @ManyToOne(fetch = FetchType.LAZY)
        val role: Role? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        val permission: Permission? = null,


        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "role_permission_rule", joinColumns = [JoinColumn(name = "role_permission_id")],
                inverseJoinColumns = [JoinColumn(name = "rule_id")])
        val rules: MutableList<Rule> = mutableListOf()

) : BaseEntity()
