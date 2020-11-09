package ${project.packageName}.service

import com.github.b1412.api.service.BaseService
import ${project.packageName}.dao.${entity.name}Dao
import ${project.packageName}.entity.${entity.name}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ${entity.name}Service(
@Autowired
val dao: ${entity.name}Dao
) : BaseService<${entity.name}, Long>(dao = dao)
