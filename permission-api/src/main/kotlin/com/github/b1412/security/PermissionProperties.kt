package com.github.b1412.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("permission")
class PermissionProperties {

    var jwt: Jwt = Jwt()

    var user: User = User()

    class Jwt {
        var header = "Authorization"
        var expiresIn: Long? = 864000L
        var secret = "queenvictoria"
        var cookie = "AUTH-TOKEN"
        var param = "token"
        var anonymousUrls: List<String> = mutableListOf()
    }

    class User {
        var needVerify = false
    }

    class Jpa {
        var entityPackages: String? = null
    }
}
