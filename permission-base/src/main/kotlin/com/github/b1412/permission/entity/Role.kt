package com.github.b1412.permission.entity

import com.github.b1412.api.entity.BaseEntity
import org.hibernate.annotations.Formula
import java.io.Serializable
import javax.persistence.*

@Entity
data class Role(
    var name: String?,

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    var users: MutableList<User> = mutableListOf(),


    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "role_id")
    val rolePermissions: MutableList<RolePermission> = mutableListOf(),

    @Formula(value="(SELECT COUNT(*) FROM role_permission rp WHERE rp.role_id = id)")
    val count: Int

) : BaseEntity(), Serializable {
    override fun toString(): String {
        return "Role(name='$name')"
    }
}
