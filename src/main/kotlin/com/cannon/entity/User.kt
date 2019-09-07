package com.cannon.entity

import javax.persistence.*

@Entity
data class User(
        var login: String,
        var address: String,
        var email: String,
        var notes: String,
        @OneToOne(fetch = FetchType.LAZY)
        var branch: Branch? = null,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.REMOVE], mappedBy = "user")
        var docs: MutableList<Doc> = mutableListOf(),
        @ManyToOne(fetch = FetchType.LAZY)
        var role: Role? = null,
        var clientId: String? = null,
        var expiresIn: Long? = null
) : BaseEntity()
