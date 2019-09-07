package com.cannon.entity

import javax.persistence.Entity

@Entity
data class Blog(
        var title: String
) : BaseEntity()
