package com.cannon.dao

import com.cannon.bean.Doc
import com.cannon.bean.Role
import com.cannon.bean.User
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Session
import org.hibernate.stat.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.EntityManager


class DocDaoTests : AbstractJpaTest() {

    @Autowired
    lateinit var docDao: DocDao

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var roleDao: RoleDao

    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var statistics: Statistics
    @BeforeEach
    fun setup() {
        //given
        val user = User(login = "name", address = "address", email = "email", notes = "notes")
        val user2 = User(login = "name2", address = "address2", email = "email2", notes = "notes2")

        val doc = Doc(name = "Spring Framework 4.0 goes GA", user = user)
        val doc2 = Doc(name = "Spring Framework 5.0 goes GA", user = user)

        val role = Role(name = "admin")

        user.docs.add(doc)
        user.docs.add(doc2)
        roleDao.save(role)
        user.role = role
        user2.role = role
        userDao.save(user)
        userDao.save(user2)

        val session = entityManager.unwrap(Session::class.java)
        statistics = session.sessionFactory.statistics
        session.clear()
        statistics.isStatisticsEnabled = true
        statistics.clear()
    }

    @Test
    fun `don't load users when find role`() {
        val role = roleDao.findByIdOrNull(1L)!!
        assertThat(role).isNotNull
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(1)
    }


    @Test
    fun `lazy load docs`() {
        val user = userDao.findByIdOrNull(1L)!!

        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(1)

        statistics.clear()

        user.role.toString()
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(1)
    }


    @Test
    fun `entity graph`() {
        val graph = entityManager.createEntityGraph(Role::class.java)
        graph.addAttributeNodes("users")
        val role = entityManager.find(Role::class.java, 1L, mapOf("javax.persistence.fetchgraph" to graph))

        role.users.forEach { it.docs.toString() }

        assertThat(role.users.size).isEqualTo(2)
        assertThat(statistics.prepareStatementCount).isEqualTo(3)

    }


    @Test
    fun `entity graph 2`() {
        val graph = entityManager.createEntityGraph(Role::class.java)
        val userGraph = graph.addSubgraph<List<User>>("users")
        userGraph.addAttributeNodes("docs")
        val role = entityManager.find(Role::class.java, 1L, mapOf("javax.persistence.fetchgraph" to graph))
    }


    @Test
    fun `Role`() {
        //when
        val user = userDao.findByIdOrNull(1L)
        println(user)
    }

    @Test
    fun `First level cache`() {
        //when
        repeat(10) {
            docDao.findByIdOrNull(1L)
        }
    }

    @Test
    fun `Save docs automatically when user has been saved`() {
        //when
        val docs = docDao.findAll()
        assertThat(docs.size).isEqualTo(2)
    }

    @Test
    fun `delete doc will clear associated User's docs`() {
        docDao.deleteById(1L)
        entityManager.flush()
        val user = userDao.findByIdOrNull(1L)!!
        assertThat(user.docs.size).isEqualTo(1)
    }
}