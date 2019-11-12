package ${project.packageName}.controller

import ${project.packageName}.controller.base.Base${entity.name}Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ${project.packageName}.service.${entity.name}Service
import org.springframework.beans.factory.annotation.Autowired
import com.github.leon.aci.extenstions.responseEntityOk
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/v1/${Utils.lowerHyphen(entity.name)}")
class ${entity.name}Controller(

) : Base${entity.name}Controller() {

    <#if entity.excelExport>
    @GetMapping("excel")
    override fun excel(pageable: Pageable, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Page<${entity.name}>> {
        return super.excel(pageable, request, response)
    }
    </#if>

}