package com.github.b1412.permission.entity


import com.github.b1412.api.entity.BaseEntity
import com.github.b1412.permission.enums.AttachmentType
import com.github.b1412.generator.metadata.EntityFeature
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@EntityFeature
@Entity
data class Attachment(

        @NotNull
        var name: String = "",

        var contentType: String? = null,

        @NotNull
        var size: Long = -1,

        @NotNull
        var originalFilename: String = "",

        var notes: String? = null,

        @NotNull
        var fullPath: String = "",
        @NotNull
        @Enumerated(value = EnumType.STRING)
        var type: AttachmentType = AttachmentType.UPLOAD

)  : BaseEntity()
