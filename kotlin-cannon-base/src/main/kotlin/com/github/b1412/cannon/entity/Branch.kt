package com.github.b1412.cannon.entity

import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.*
import com.github.b1412.api.entity.BaseEntity
@Entity
data class Branch(
        var name: String? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        val parent: Branch? = null,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
        val children: MutableList<Branch> = mutableListOf(),

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch")
        var users: MutableList<User> = mutableListOf(),

        @Type(type = "yes_no")
        var active: Boolean? = null
) : BaseEntity(), Serializable
