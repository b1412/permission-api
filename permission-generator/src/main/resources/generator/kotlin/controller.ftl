package ${project.packageName}.controller

import ${project.packageName}.controller.base.Base${entity.name}Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ${project.packageName}.service.${entity.name}Service

@RestController
@RequestMapping("/v1/${entity.lowerHyphenName}")
class ${entity.name}Controller : Base${entity.name}Controller()