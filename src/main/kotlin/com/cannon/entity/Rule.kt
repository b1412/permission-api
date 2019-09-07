package com.cannon.entity

import org.hibernate.annotations.Type
import javax.persistence.Entity

@Entity
data class Rule(
        val name: String,
        val type: String? = null,
        @Type(type = "yes_no")
        val enable: Boolean = true
) : BaseEntity()
