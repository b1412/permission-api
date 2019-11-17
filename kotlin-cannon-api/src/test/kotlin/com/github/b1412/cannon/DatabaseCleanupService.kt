package com.github.b1412.cannon

import org.hibernate.Session
import org.hibernate.metamodel.internal.MetamodelImpl
import org.hibernate.persister.entity.SingleTableEntityPersister
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class DatabaseCleanupService(
        private val entityManager: EntityManager
) {

    fun allTableNames(): List<String> {
        val session = entityManager.unwrap(Session::class.java)
        val sessionFactory = session.sessionFactory
        return (sessionFactory.metamodel as MetamodelImpl)
                .entityPersisters()
                .map { (it.value as SingleTableEntityPersister).tableName }
    }

    fun truncate() {
        entityManager.flush()
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        allTableNames().forEach {
            val sql = "TRUNCATE TABLE $it"
            println(sql)
            val sql2 = "ALTER TABLE $it ALTER COLUMN id RESTART WITH 1"
            entityManager.createNativeQuery(sql).executeUpdate()
            entityManager.createNativeQuery(sql2).executeUpdate()
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }
}