package com.github.b1412.cannon.entity

import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.Entity

@Entity
data class Rule(
        val name: String,
        val type: String? = null,
        @Type(type = "yes_no")
        val enable: Boolean = true
) : BaseEntity(), Serializable
