package com.github.b1412.permission.dao

import com.github.b1412.permission.entity.Branch
import com.github.b1412.permission.entity.Role
import com.github.b1412.permission.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.time.temporal.ChronoUnit

class BranchDaoTest : AbstractJpaTest() {
    @Autowired
    lateinit var branchDao: BranchDao

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var roleDao: RoleDao

    @BeforeEach
    fun setup() {
        //given
        val role1 = Role(name = "admin")
        val role2 = Role(name = "manager")
        roleDao.saveAll(listOf(role1, role2))
        val user1 = User(login = "login1", address = "address1", email = "email1", notes = "notes1", active = true, role = role1)
        val user2 = User(login = "login2", address = "address2", email = "email2", notes = "notes2", active = false, role = role2)
        userDao.save(user1)
        userDao.save(user2)
        val branchA = Branch(name = "branchA", notes = "branchA is on foo street", active = true, users = mutableListOf(user1))
        val branchB = Branch(name = "branchB", notes = "branchB is close to branchA", active = false, users = mutableListOf(user2))
        user1.branch = branchA
        user2.branch = branchB
        branchDao.save(branchA)
        branchDao.save(branchB)
    }

    // http://localhost:8080/branch
    @Test
    fun `return branches without embedded when search by filter without parameters`() {
        //when
        val branches = branchDao.searchByFilter(mapOf(), Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[1].id).isEqualTo(2)
    }

    // http://localhost:8080/branch?id=1
    @Test
    fun `return branches when search by filter with parameter f_id=1`() {
        //when
        val queryMap = mapOf("id_eq" to "1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(1)
        assertThat(branches.content[0].id).isEqualTo(1)
    }

    // http://localhost:8080/branch?name_like
    @Test
    fun `return branches when search by filter with parameters f_name=1 and f_name_op=like`() {
        //when
        val queryMap = mapOf("name_like" to "branch")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[1].id).isEqualTo(2)
    }

    //http://localhost:8080/branch?name=branchA
    @Test
    fun `return branches when search by filter with parameter name=branchA`() {
        //when
        val queryMap = mapOf("name_eq" to "branchA")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(1)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[0].name).isEqualTo("branchA")
    }

    //http://localhost:8080/branch?users.notes=4
    @Test
    fun `return branches when search by filter with parameter users*notes=4`() {
        //when
        val queryMap = mapOf("users.notes_eq" to "notes1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(1)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[0].users[0].id).isEqualTo(1)
        assertThat(branches.content[0].users[0].notes).isEqualTo("notes1")
    }

    //http://localhost:8080/branch?f_users.role.id=1
    @Test
    fun `return branches when search by filter with parameter users*role*id=1`() {
        //when
        val queryMap = mapOf("users.role.id_eq" to "1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(1)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[0].users[0].id).isEqualTo(1)
    }

    // http://localhost:8080/branch?f_name=branchA,branchB&f_name_op=in
    @Test
    fun `return branches when search by filter with parameter name_in=branchA,branchB`() {
        //when
        val queryMap = mapOf("name_in" to "branchA,branchB")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter id_between=1,2`() {
        //when
        val queryMap = mapOf("id_between" to "1,2")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter active=false`() {
        //when
        val queryMap = mapOf("active_eq" to "false")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(1)
        assertThat(branches.content[0].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter active_in=false,true`() {
        //when
        val queryMap = mapOf("active_in" to "false,true")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `search on multiple fields`() {
        //when
        val queryMap = mapOf("name-notes_like" to "branchA")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        assertThat(branches.totalElements).isEqualTo(2)
        assertThat(branches.content[0].id).isEqualTo(1)
        assertThat(branches.content[0].name).contains("branchA")
        assertThat(branches.content[1].id).isEqualTo(2)
        assertThat(branches.content[1].notes).contains("branchA")
    }

    @Test
    fun `search date`() {
        //when
        val end = Instant.now().toEpochMilli()
        val start = Instant.now().minus(5,ChronoUnit.MINUTES).toEpochMilli()
        val queryMap = mapOf("createdAt_between" to "${start},${end}")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())

        //then
        println(branches.content[0].createdAt)
        assertThat(branches.content[0].createdAt).isNotNull()
    }

}
