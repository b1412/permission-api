package com.github.b1412.cannon.entity

import java.io.Serializable
import javax.persistence.Entity

@Entity
data class Rule(
        val name: String

) : BaseEntity(), Serializable
