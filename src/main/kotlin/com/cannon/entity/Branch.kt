package com.cannon.entity

import javax.persistence.*

@Entity
data class Branch(
        var name: String,

        var number: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        val parent: Branch? = null,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
        val children: MutableList<Branch> = mutableListOf(),

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch")
        var users: MutableList<User> = mutableListOf()

) : BaseEntity()