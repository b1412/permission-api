package com.github.b1412.cannon.service.rule


import com.github.b1412.cannon.entity.User



interface SecurityFilter {

    fun currentUser(): User

    fun query(method: String, requestURI: String):  Map<String, String>

}
