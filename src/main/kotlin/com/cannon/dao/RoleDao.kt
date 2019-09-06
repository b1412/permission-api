package com.cannon.dao

import com.cannon.bean.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleDao : JpaRepository<Role, Long>


