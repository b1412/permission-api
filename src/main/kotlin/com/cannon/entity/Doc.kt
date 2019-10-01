package com.cannon.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
data class Doc(
        var name: String,
        @ManyToOne(fetch = FetchType.LAZY)
        var user: User
) : BaseEntity()