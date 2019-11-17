package com.github.b1412.cannon.controller

import com.github.b1412.cannon.dao.BranchDao
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/branch")
class BranchController(
        val branchDao: BranchDao
) {
    @GetMapping
    fun list(@RequestParam filter: Map<String, String>, request: HttpServletRequest) =
            branchDao.searchBySecurity(request.method, request.requestURI, filter)
}