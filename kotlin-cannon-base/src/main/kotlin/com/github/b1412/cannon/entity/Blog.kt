package com.github.b1412.cannon.entity

import javax.persistence.Entity

@Entity
data class Blog(
        var title: String
) : BaseEntity()
