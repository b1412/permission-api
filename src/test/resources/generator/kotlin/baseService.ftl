package ${project.packageName}.service.base


import ${project.packageName}.entity.${entity.name}
import com.github.leon.aci.service.base.BaseService
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
class Base${entity.name}Service : BaseService<${entity.name}, Long>()

