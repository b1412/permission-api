package com.github.b1412.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("permission")
class ApplicationProperties {

    var jwt: Jwt = Jwt()

    var user: User = User()


    class Jwt {
        var header = "Authorization"
        var expiresIn: Long? = 864000L
        var secret = "queenvictoria"
        var cookie = "AUTH-TOKEN"
        var param = "token"
        var anonymousUrls: String? = null
    }

    class User {
        var needVerify = false
    }
}
