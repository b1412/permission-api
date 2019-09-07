package com.cannon.dao

import com.cannon.entity.Branch
import com.cannon.entity.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BranchDaoTest : AbstractJpaTest() {
    @Autowired
    lateinit var branchDao: BranchDao

    @Autowired
    lateinit var userDao: UserDao

    @BeforeEach
    fun setup() {
        //given
        val user1 = User(login = "login1", address = "address1", email = "email1", notes = "notes1")
        val user2 = User(login = "login2", address = "address2", email = "email2", notes = "notes2")
        userDao.save(user1)
        userDao.save(user2)
        val branchA = Branch(name = "branchA", number = "1", users = mutableListOf(user1))
        val branchB = Branch(name = "branchB", number = "2", users = mutableListOf(user2))
        user1.branch = branchA
        user2.branch = branchB
        branchDao.save(branchA)
        branchDao.save(branchB)
    }

    // http://localhost:8080/branch
    @Test
    fun `return branches without embedded when search by filter without parameters`() {
        //when
        val branches = branchDao.searchByFilter(mapOf())
        //then
        Assertions.assertThat(branches.size).isEqualTo(2)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
        Assertions.assertThat(branches[1].id).isEqualTo(2)
    }

    // http://localhost:8080/branch?f_id=1
    @Test
    fun `return branches when search by filter with parameter f_id=1`() {
        //when
        val queryMap = mapOf("f_id" to "1")
        val branches = branchDao.searchByFilter(queryMap)
        //then
        Assertions.assertThat(branches.size).isEqualTo(1)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
    }

    // http://localhost:8080/branch?f_name=1&f_name_op=like
    @Test
    fun `return branches when search by filter with parameters f_name=1 and f_name_op=like`() {
        //when
        val queryMap = mapOf("f_name" to "branch", "f_name_op" to "like")
        val branches = branchDao.searchByFilter(queryMap)
        //then
        Assertions.assertThat(branches.size).isEqualTo(2)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
        Assertions.assertThat(branches[1].id).isEqualTo(2)
    }

    //http://localhost:8080/branch?f_name=b1
    @Test
    fun `return branches when search by filter with parameter f_name=b1`() {
        //when
        val queryMap = mapOf("f_name" to "branchA")
        val branches = branchDao.searchByFilter(queryMap)
        //then
        Assertions.assertThat(branches.size).isEqualTo(1)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
        Assertions.assertThat(branches[0].name).isEqualTo("branchA")
    }

    //http://localhost:8080/branch?f_users.notes=4
    @Test
    fun `return branches when search by filter with parameter f_users*notes=4`() {
        //when
        val queryMap = mapOf("f_users.notes" to "notes1")
        val branches = branchDao.searchByFilter(queryMap)
        //then
        Assertions.assertThat(branches.size).isEqualTo(1)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
        Assertions.assertThat(branches[0].users[0].id).isEqualTo(1)
        Assertions.assertThat(branches[0].users[0].notes).isEqualTo("notes1")
        // TODO
        /*
        PROBLEM: the sql is inner join when query the association fields, but expected is left join!
        */
    }

    // http://localhost:8080/branch?f_name=branchA,branchB&f_name_op=in
    @Test
    fun `return branches when search by filter with parameter f_name=branchA,branchB&f_name_op=in`() {
        //when
        val queryMap = mapOf("f_name" to "branchA,branchB", "f_name_op" to "in")
        val branches = branchDao.searchByFilter(queryMap)
        //then
        Assertions.assertThat(branches.size).isEqualTo(2)
        Assertions.assertThat(branches[0].id).isEqualTo(1)
        Assertions.assertThat(branches[1].id).isEqualTo(2)
    }

//    @Test
//    fun `return branches when search by filter with parameter f_id=1,2&f_name_op=between`() {
//        //when
//        val queryMap = mapOf("f_id" to "1,2", "f_id_op" to "between")
//        val branches = branchDao.searchByFilter(queryMap)
//        //then
//        Assertions.assertThat(branches.size).isEqualTo(2)
//        Assertions.assertThat(branches[0].id).isEqualTo(1)
//        Assertions.assertThat(branches[1].id).isEqualTo(2)
//    }
}
