package com.github.b1412.api.entity

import com.github.b1412.permission.entity.User
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime
import javax.persistence.*

@Where(clause = "deletedAt is not null")
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
        var updatedAt: ZonedDateTime? = null,

        var deletedAt: ZonedDateTime? = null,

        @CreatedBy
        @ManyToOne
        @JoinColumn(name = "creator_id")
        var creator: User? = null,

        @LastModifiedBy
        @ManyToOne
        @JoinColumn(name = "modifier_id")
        var modifier: User? = null,
)
