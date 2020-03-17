package com.github.b1412.cannon.dao


import com.github.b1412.cannon.entity.Branch
import com.github.b1412.cannon.entity.Role
import com.github.b1412.cannon.entity.User
import com.github.b1412.cannon.jpa.V2UrlMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

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
        val branchA = Branch(name = "branchA", active = true, users = mutableListOf(user1))
        val branchB = Branch(name = "branchB", active = false, users = mutableListOf(user2))
        user1.branch = branchA
        user2.branch = branchB
        branchDao.save(branchA)
        branchDao.save(branchB)
    }

    @Test
    fun `return branches without embedded when search by filter without parameters`() {
        //when
        val branches = branchDao.searchByFilter(mapOf(), Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(2)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter f_id=1`() {
        //when
        val queryMap = mapOf("f_id" to "1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
    }

    @Test
    fun `return branches when search by filter with parameter id_==1`() {
        //when
        val queryMap = mapOf("id_=" to "1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged(), V2UrlMapper())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
    }

    @Test
    fun `return branches when search by filter with parameters f_name=1 and f_name_op=like`() {
        //when
        val queryMap = mapOf("f_name" to "branch", "f_name_op" to "like")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(2)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter f_name=b1`() {
        //when
        val queryMap = mapOf("f_name" to "branchA")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[0].name).isEqualTo("branchA")
    }

    @Test
    fun `return branches when search by filter with parameter f_users*notes=4`() {
        //when
        val queryMap = mapOf("f_users.notes" to "notes1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[0].users[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[0].users[0].notes).isEqualTo("notes1")
    }

    @Test
    fun `return branches when search by filter with parameter f_users*role*id=1`() {
        //when
        val queryMap = mapOf("f_users.role.id" to "1")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[0].users[0].id).isEqualTo(1)
    }

    @Test
    fun `return branches when search by filter with parameter f_name=branchA,branchB&f_name_op=in`() {
        //when
        val queryMap = mapOf("f_name" to "branchA,branchB", "f_name_op" to "in")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(2)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter f_id=1,2&f_name_op=between`() {
        //when
        val queryMap = mapOf("f_id" to "1,2", "f_id_op" to "between")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(2)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[1].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter f_active=false`() {
        //when
        val queryMap = mapOf("f_active" to "false")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(1)
        Assertions.assertThat(branches.content[0].id).isEqualTo(2)
    }

    @Test
    fun `return branches when search by filter with parameter f_active=false,true&f_active_op=in`() {
        //when
        val queryMap = mapOf("f_active" to "false,true", "f_active_op" to "in")
        val branches = branchDao.searchByFilter(queryMap, Pageable.unpaged())
        //then
        Assertions.assertThat(branches.totalElements).isEqualTo(2)
        Assertions.assertThat(branches.content[0].id).isEqualTo(1)
        Assertions.assertThat(branches.content[1].id).isEqualTo(2)
    }
}
