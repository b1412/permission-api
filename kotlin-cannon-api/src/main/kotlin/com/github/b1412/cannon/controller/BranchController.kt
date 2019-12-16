package com.github.b1412.cannon.controller

import com.github.b1412.cannon.controller.base.BaseBranchController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/branch")
class BranchController : BaseBranchController()