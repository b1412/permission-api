package com.github.b1412.cannon.entity

import org.hibernate.annotations.Type
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
data class User(
        var login: String,
        var address: String,
        var email: String,
        var notes: String,
        @OneToOne(fetch = FetchType.LAZY)
        var branch: Branch? = null,
        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.REMOVE], mappedBy = "user")
        var docs: MutableList<Doc> = mutableListOf(),
        @ManyToOne(fetch = FetchType.LAZY)
        var role: Role? = null,
        var clientId: String? = null,
        var expiresIn: Long? = null,

        @Type(type = "yes_no")
        var active: Boolean? = null,

        private var username: String = "",

        private var password: String? = null,

        @Transient
        var grantedAuthorities: MutableList<GrantedAuthority> = mutableListOf()

) : BaseEntity(), UserDetails {
    override fun getUsername(): String {
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

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
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

}
