package com.github.b1412.permission.service

import arrow.core.Either
import com.github.b1412.api.service.BaseService
import com.github.b1412.aws.s3.UploadUtil
import com.github.b1412.permission.dao.AttachmentDao
import com.github.b1412.permission.entity.Attachment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AttachmentService(
        @Autowired
        val dao: AttachmentDao
) : BaseService<Attachment, Long>(dao = dao) {
    @Autowired
    private val uploadUtil: UploadUtil? = null

    fun createFile(tempFile: MultipartFile): Either<String, Attachment> {
        if (tempFile.isEmpty) {
            return Either.left("no file uploaded")
        }
        val name = uploadUtil!!.write(tempFile, "")
        val attachment = Attachment(name = name,
                contentType = tempFile.contentType!!,
                size = tempFile.size,
                originalFilename = tempFile.originalFilename!!,
                fullPath = "/v1/attachment/download?filename=$name")
        save(attachment)
        return Either.right(attachment)
    }
}
