package com.cannon.bean

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class Role(
        var name: String = "",
        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
        var users: MutableList<User> = mutableListOf()
) : BaseEntity()