package com.github.b1412.permission.controller


import com.github.b1412.permission.util.findClasses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class CommonController(
        @Autowired
        val context: ApplicationContext
) {

    @GetMapping("/enum/{name}")
    fun enum(@PathVariable name: String): ResponseEntity<*> {

        val result = findClasses(Enum::class.java, "classpath*:com/github/b1412/**/*.class")
        val enum = result.first { it.simpleName == name }
        val values = enum.getDeclaredMethod("values").invoke(enum) as Array<*>
        val list = values.map { mapOf("label" to it.toString(), "value" to it.toString()) }

//        val value = (Reflect.on(enumClazz).call("values").get() as Array<*>)
//                .map { it.toString().remainLastIndexOf(".") }
//                .toList()
        return ResponseEntity.ok(list)
    }

//
//    @PostMapping(value = ["exist/{entity}/{f}"])
//    fun existenceCheck(@PathVariable entity: String, @PathVariable f: String, v: String, id: Long?): ResponseEntity<Boolean> {
//        val entityName = CaseFormat.LOWER_HYPHEN.converterTo(CaseFormat.LOWER_CAMEL).convert(entity)
//        val service = context.getBean("${entityName}Service") as BaseService<*, *>
//        return Option.monad().binding {
//            val field = f.toOption().bind()
//            val value = v.toOption().bind()
//            val filter = Filter(conditions = listOf(Condition(
//                    fieldName = field,
//                    value = value,
//                    operator = Filter.OPERATOR_EQ
//            )))
//            id.toOption().forEach {
//                filter.conditions = filter.conditions + Condition(fieldName = "id", value = id, operator = Filter.OPERATOR_NOT_EQ)
//            }
//
//            val result = Try { service.findByFilter(filter) }.toOption().bind()
//            result.isEmpty()
//
//        }.fix().getOrElse { false }.responseEntityOk()
//    }
//
//
//    @GetMapping("/enum-select/{name}")
//    fun enumSelect(@PathVariable name: String): ResponseEntity<List<Map<String, String>>> {
//        val enumClazz: Class<out Enum<*>> = ApplicationProperties.enumPackages
//                .map { p -> Try { Reflect.on("$p.$name").get<Any>() as Class<out Enum<*>> } }
//                .firstOption { it.isSuccess() }.get().get()
//        val value = (Reflect.on(enumClazz).call("values").get() as Array<*>)
//                .map { it.toString().remainLastIndexOf(".") }
//                .toList()
//                .map { mapOf("display" to it, "id" to it) }
//        return ResponseEntity.ok(value)
//    }

}