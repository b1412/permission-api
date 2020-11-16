package com.github.b1412.permission.entity

import com.github.b1412.api.entity.BaseEntity
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class RolePermission(

        @ManyToOne(fetch = FetchType.LAZY)
        @NotNull
        val permission: Permission? = null,

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "role_permission_rule",
                joinColumns = [JoinColumn(name = "role_permission_id")],
                inverseJoinColumns = [JoinColumn(name = "rule_id")])
        val rules: MutableList<Rule> = mutableListOf()

) : BaseEntity(), Serializable
