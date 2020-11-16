package ${project.packageName}.dao

import com.github.b1412.api.dao.BaseDao
import ${project.packageName}.entity.${entity.name}
import org.springframework.stereotype.Repository

@Repository
interface ${entity.name}Dao : BaseDao<${entity.name}, Long>
