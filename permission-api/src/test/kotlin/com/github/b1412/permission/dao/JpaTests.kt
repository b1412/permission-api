package com.github.b1412.permission.dao

import com.github.b1412.permission.entity.Branch
import com.github.b1412.permission.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Session
import org.hibernate.graph.EntityGraphs
import org.hibernate.graph.GraphParser
import org.hibernate.stat.Statistics
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.EntityManager

class JpaTests : AbstractJpaTest() {

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var branchDao: BranchDao

    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var statistics: Statistics

    @BeforeEach
    fun setup() {
        //given
        val parentBranch = Branch(name = "headquarter")
        branchDao.save(parentBranch)
        val branch1 = Branch(name = "Spring Framework 4.0 goes GA")
        val branch2 = Branch(name = "Spring Framework 5.0 goes GA")


        val user1 = User(login = "name", address = "address", email = "email", notes = "notes")
        val user2 = User(login = "name2", address = "address2", email = "email2", notes = "notes2")
        userDao.save(user1)
        userDao.save(user2)

        branch1.users.add(user1)
        branch1.users.add(user2)
        branchDao.save(parentBranch)

        branch1.parent = parentBranch
        branch2.parent = parentBranch
        branchDao.save(branch1)
        branchDao.save(branch2)

        val session = entityManager.unwrap(Session::class.java)
        statistics = session.sessionFactory.statistics
        session.clear()
        statistics.isStatisticsEnabled = true
        statistics.clear()
    }

    @Test
    fun `don't load children load parent branch, lazy load`() {
        val parentBranch = branchDao.findByIdOrNull(1L)!!
        assertThat(parentBranch).isNotNull
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(1)
    }

    @Test
    fun `entity graph`() {
        // when
        val graph = entityManager.createEntityGraph(Branch::class.java)
        graph.addAttributeNodes("children")
        val parentBranch = entityManager.find(Branch::class.java, 1L, mapOf("javax.persistence.fetchgraph" to graph))

        // then
        assertThat(parentBranch.children.size).isEqualTo(2)
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
    }


    @Test
    fun `entity graph from string`() {
        // when
        val graph = GraphParser.parse(Branch::class.java, "children", entityManager)
        val parentBranch = entityManager.find(Branch::class.java, 1L, mapOf("javax.persistence.fetchgraph" to graph))

        // then
        assertThat(parentBranch.children.size).isEqualTo(2)
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(3)


    }

    // @Test
    fun `entity graph from fields`() {
        // when
        val graphs = listOf(GraphParser.parse(Branch::class.java, "parent", entityManager),
                GraphParser.parse(Branch::class.java, "users", entityManager))
        val graph = EntityGraphs.merge(entityManager, Branch::class.java, *graphs.toTypedArray())

        val parentBranch = entityManager.find(Branch::class.java, 1L, mapOf("javax.persistence.fetchgraph" to graph))

        // then
        assertThat(parentBranch.children.size).isEqualTo(2)
        assertThat(statistics.prepareStatementCount).isEqualTo(1)
        assertThat(statistics.entityLoadCount).isEqualTo(4)
    }
}
