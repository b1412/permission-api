package com.cannon.entity

import org.hibernate.annotations.Type
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity
data class Rule(
        val name: String,
        val params: String? = null,
        val type: String? = null,
        @Type(type = "yes_no")
        @field:NotNull
        val enable: Boolean = true
) : BaseEntity()
