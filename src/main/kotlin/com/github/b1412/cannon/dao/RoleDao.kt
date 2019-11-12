package com.github.b1412.cannon.dao

import com.github.b1412.cannon.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleDao : JpaRepository<Role, Long>


