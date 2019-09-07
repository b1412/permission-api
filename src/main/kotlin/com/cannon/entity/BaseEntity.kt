package com.cannon.entity

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

        @CreatedDate
        var createdAt: ZonedDateTime? = null,

        @LastModifiedDate
        var updatedAt: ZonedDateTime? = null
)