package com.github.b1412.permission.config

import com.github.b1412.api.dao.BaseDaoImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaAuditing(
        dateTimeProviderRef = "customDateTimeProvider",
        auditorAwareRef = "securityAuditor"
)
@EnableJpaRepositories(basePackages = [
    "com.github.b1412.*.dao"
], repositoryBaseClass = BaseDaoImpl::class)
@EnableTransactionManagement
class JpaConfig
