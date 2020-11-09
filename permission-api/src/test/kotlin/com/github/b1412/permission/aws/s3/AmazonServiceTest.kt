package com.github.b1412.permission.aws.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.github.b1412.aws.s3.AmazonProperties
import com.github.b1412.aws.s3.AmazonService
import com.github.b1412.aws.s3.UploadData
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


class AmazonServiceTest {

    //@Test
    fun test() {
        val amazonProperties = AmazonProperties(
                bucketName = "zran-test",
                accessKey = "AKIASDU2BIWHNSYS57DQ",
                secretKey = "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",
                regionLink = "https://s3-ap-southeast-2.amazonaws.com",
                key = "test/")

        val s3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                        AWSStaticCredentialsProvider(
                                BasicAWSCredentials(amazonProperties.accessKey, amazonProperties.secretKey)
                        )
                ).withRegion(Regions.AP_SOUTHEAST_2).build()
        val service = AmazonService(amazonProperties, s3)
        val fileName = "sample.jpg"
        val fileInuptStream =  AmazonServiceTest::class.java.classLoader.getResourceAsStream(fileName)
        val bImage: BufferedImage  = ImageIO.read(fileInuptStream)
        val bos = ByteArrayOutputStream()
        ImageIO.write(bImage, "jpg", bos)
        val data = UploadData(name = fileName, data = bos.toByteArray())
        val result  = service.upload(data)
        println(result)
    }
    // write test cases here
}

