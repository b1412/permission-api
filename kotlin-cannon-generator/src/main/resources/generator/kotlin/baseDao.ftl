package ${project.packageName}.dao.base

import com.github.leon.aci.dao.base.BaseDao
import ${project.packageName}.entity.${entity.name}
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface Base${entity.name}Dao : BaseDao<${entity.name}, Long>
