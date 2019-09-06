package com.cannon.bean

import javax.persistence.*

@Entity
class Branch(
        var name: String,
        var number: String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        val parent: Branch? = null,
        @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
        var users: MutableList<User>
) : BaseEntity()