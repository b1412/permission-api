package ${project.packageName}.controller.base

import com.github.leon.aci.util.QueryBuilder
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
import com.github.leon.aci.web.base.BaseController
import com.github.leon.files.PoiExporter
import com.github.leon.bean.JpaBeanUtil
import org.springframework.transaction.annotation.Transactional


abstract class Base${entity.name}Controller(

) : BaseController<${entity.name}, Long>() {

    @PostMapping("graph")
    override fun graph(@RequestBody body: String, pageable: Pageable, request: HttpServletRequest): ResponseEntity<Page<${entity.name}>> {
        val map = QueryBuilder.queryList(QueryBuilder.graphqlPlayload(body))
        val page = baseService.findBySecurity(request.method, request.requestURI, map, pageable)
        return ResponseEntity.ok(page)
    }

    @GetMapping("easyui")
    override fun easyui(pageable: Pageable, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val page = baseService.findByRequestParameters(request.parameterMap, pageable)
        val total = page.totalElements
        val rows = page.content
        val map = mapOf("total" to total, "rows" to rows)
        return ResponseEntity.ok(map)
    }

    @GetMapping
    override fun page(pageable: Pageable, request: HttpServletRequest): ResponseEntity<Page<${entity.name}>> {
        <#if entity.security >
        return super.page(pageable, request)
        <#else>
        return ResponseEntity.ok(baseService.findByRequestParameters(request.parameterMap, pageable))
        </#if>
    }

    @GetMapping("{id}")
    override fun findOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<${entity.name}> {
        <#if entity.security >
        return super.findOne(id, request)
        <#else>
        return ResponseEntity.ok(baseService.findOne(id))
        </#if>
    }

    @Transactional
    @PostMapping
    override fun saveOne(@Validated @RequestBody input: ${entity.name}, request: HttpServletRequest): ResponseEntity<*> {
        <#if entity.security >
        return super.saveOne(input, request)
        <#else>
        return ResponseEntity.ok(baseService.save(input))
        </#if>
    }

    @Transactional
    @PutMapping("{id}")
    override fun updateOne(@PathVariable id: Long, @Validated @RequestBody input: ${entity.name}, request: HttpServletRequest): ResponseEntity<*> {
        <#if entity.security >
        return super.updateOne(id, input, request)
        <#else>
        val persisted = baseService.findOne(id)
        JpaBeanUtil.copyNonNullProperties(input as Any, persisted as Any)
        baseService.save(persisted)
        return ResponseEntity.ok(persisted)
        </#if>

    }

    @DeleteMapping("{id}")
    override fun deleteOne(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<*> {
        <#if entity.security >
        return super.deleteOne(id, request)
        <#else>
        baseService.delete(id)
        return ResponseEntity.noContent().build<Any>()
        </#if>
    }

}