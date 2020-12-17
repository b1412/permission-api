package com.github.b1412.permission.entity

import com.github.b1412.api.entity.BaseEntity
import org.hibernate.annotations.Type
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import javax.persistence.*

@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["client_id", "username"]),
        UniqueConstraint(columnNames = ["client_id", "email"])
    ]
)
@Entity
data class User(
    var login: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    var address: String? = null,
    var email: String? = null,
    var notes: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    var branch: Branch? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    var role: Role? = null,

    var clientId: String? = null,
    var expiresIn: Long? = null,

    @Type(type = "yes_no")
    var active: Boolean? = null,

    private var username: String? = null,

    private var password: String? = null,

    @Transient
    var confirmPassword: String? = null,

    @Transient
    var grantedAuthorities: MutableList<SimpleGrantedAuthority> = mutableListOf()

) : BaseEntity(), UserDetails, Serializable {
    override fun getUsername(): String? {
        return username
    }

    override fun getPassword(): String? {
        return password

    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities(): MutableCollection<SimpleGrantedAuthority> {
        return grantedAuthorities
    }

    override fun isEnabled(): Boolean {
        return true
    }


    override fun isCredentialsNonExpired(): Boolean {
        return true
    }


    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    companion object {

        private const val serialVersionUID = 1L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (login != other.login) return false
        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (address != other.address) return false
        if (email != other.email) return false
        if (notes != other.notes) return false
        if (clientId != other.clientId) return false
        if (expiresIn != other.expiresIn) return false
        if (active != other.active) return false
        if (username != other.username) return false
        if (password != other.password) return false

        return true
    }

}
