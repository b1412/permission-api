package com.cannon.bean

import javax.persistence.Entity

@Entity
class Blog(
        var title: String
) : BaseEntity()
