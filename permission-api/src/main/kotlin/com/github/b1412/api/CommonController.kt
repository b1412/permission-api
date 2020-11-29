package com.github.b1412.api

import arrow.core.extensions.list.foldable.firstOrNone
import com.github.b1412.api.service.BaseService
import com.github.b1412.util.findClasses
import com.google.common.base.CaseFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1")
class CommonController(
        @Autowired
        val context: ApplicationContext
) {

    @GetMapping(value = ["uniqueness/{entity}/{f}"])
    fun existenceCheck(@PathVariable entity: String, @PathVariable f: String, v: String, id: Optional<Long>): ResponseEntity<*> {
        val entityName = CaseFormat.LOWER_HYPHEN.converterTo(CaseFormat.LOWER_CAMEL).convert(entity)
        val service = context.getBean("${entityName}Service") as BaseService<*, *>
        val filter = mutableMapOf("${f}_eq" to v)
        id.ifPresent {
            filter["id_ne"] = id.get().toString()
        }
        val result = service.searchOneBy(filter)
        return result.fold(
                { ResponseEntity.notFound().build<Void>() },
                { ResponseEntity.status(HttpStatus.CONFLICT).build() }
        )
    }

    @GetMapping(value = ["enum/{enum}"])
    fun enumOptions(@PathVariable enum: String): ResponseEntity<List<Map<String, String>>> {
        return findClasses(Enum::class.java, "classpath:nz/co/zran/cannon/enums/*.class")
                .firstOrNone { it.simpleName == enum }
                .toEither { }
                .map { enumClass ->
                    (enumClass.getDeclaredMethod("values")
                            .invoke(enumClass) as Array<*>)
                            .map { mapOf("key" to it.toString(), "value" to it.toString()) }
                }.fold(
                        { ResponseEntity.notFound().build() },
                        { ResponseEntity.ok(it) }
                )
    }
}