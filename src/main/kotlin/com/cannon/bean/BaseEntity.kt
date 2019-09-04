package com.cannon.bean

import com.cannon.graphql.annotation.GraphQLIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Version
        var version: Long? = null,

        @GraphQLIgnore
        @CreatedDate
        var createdAt: ZonedDateTime? = null,

        @GraphQLIgnore
        @LastModifiedDate
        var updatedAt: ZonedDateTime? = null
)