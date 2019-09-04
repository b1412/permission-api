package com.cannon.bean

import javax.persistence.*

@Entity
class User(
        var login: String,
        var address: String,
        var email: String,
        var notes: String,
        @OneToOne
        var branch: Branch? = null,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.REMOVE], mappedBy = "user")
        var docs: MutableList<Doc> = mutableListOf(),
        @ManyToOne(fetch = FetchType.LAZY)
        var role: Role? = null
) : BaseEntity()
