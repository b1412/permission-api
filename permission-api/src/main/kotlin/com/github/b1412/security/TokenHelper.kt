package com.github.b1412.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

@EnableConfigurationProperties(value = [PermissionProperties::class])
@Component
class TokenHelper(
        @Autowired
        val permissionProperties: PermissionProperties,
        @Value("\${spring.application.name}")
        val application: String
) {

    fun getUsernameFromToken(token: String): String {
        return Jwts.parser()
                    .setSigningKey(permissionProperties.jwt.secret)
                    .parseClaimsJws(token)
                    .body.subject!!

    }

    fun generateToken(username: String, clientId: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val currentDate = Date(currentTimeMillis)
        val expirationDate = Date(currentTimeMillis + permissionProperties.jwt.expiresIn!! * 1000)
        return Jwts.builder()
                .setIssuer(application)
                .setSubject("$username@@$clientId")
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(HS512, permissionProperties.jwt.secret)
                //  .claim("user", userDetails)
                .compact()
    }


    fun getToken(request: HttpServletRequest): String? {
        /**
         * Getting the token from Cookie store
         */
        val authCookie = getCookieValueByName(request, permissionProperties.jwt.cookie)
        if (authCookie != null) {
            return authCookie.value
        }
        /**
         * Getting the token from Authentication header
         * e.g Bearer your_token
         */
        val authHeader = request.getHeader(permissionProperties.jwt.header)
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else request.getParameter(permissionProperties.jwt.param)

    }

    /**
     * Find a specific HTTP cookie in a request.
     *
     * @param request The HTTP request object.
     * @param name    The cookie name to look for.
     * @return The cookie, or `null` if not found.
     */
    fun getCookieValueByName(request: HttpServletRequest, name: String): Cookie? {
        if (request.cookies == null) {
            return null
        }
        for (i in 0 until request.cookies.size) {
            if (request.cookies[i].name == name) {
                return request.cookies[i]
            }
        }
        return null
    }


}