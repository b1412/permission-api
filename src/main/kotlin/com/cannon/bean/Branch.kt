package com.cannon.bean

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class Branch(
        var name: String,
        var number: String,
        @OneToMany(mappedBy = "branch",fetch = FetchType.LAZY)
        var users: MutableList<User>
) : BaseEntity()