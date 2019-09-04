package com.cannon.bean

import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class Branch(
        var name: String,
        var number: String,
        @OneToMany(mappedBy = "branch")
        var users: MutableList<User>
) : BaseEntity()