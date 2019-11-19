package ${project.packageName}.controller.base

import com.github.b1412.cannon.controller.BaseController
import ${project.packageName}.controller.base.Base${entity.name}Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ${project.packageName}.entity.${entity.name}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestParam


abstract class Base${entity.name}Controller(

) : BaseController<${entity.name}, Long>() {

    @GetMapping
    override fun page(request: HttpServletRequest, @RequestParam filter: Map<String, String>): List<${entity.name}> {
        return super.page(request, filter)

    }

    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ${entity.name} {
        return super.findOne(id, request)
    }

    @Transactional
    @PostMapping
    override fun saveOne(@Validated @RequestBody input: ${entity.name}, request: HttpServletRequest): ResponseEntity<*> {
        return super.saveOne(input, request)

    }

    @Transactional
    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: ${entity.name}, request: HttpServletRequest): ResponseEntity<*> {
        return super.updateOne(id, input, request)
    }

    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        return super.deleteOne(id,request)

    }
}