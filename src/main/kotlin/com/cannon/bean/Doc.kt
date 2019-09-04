package com.cannon.bean

import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Doc(
        var name: String,
        @ManyToOne
        var user: User
) : BaseEntity()