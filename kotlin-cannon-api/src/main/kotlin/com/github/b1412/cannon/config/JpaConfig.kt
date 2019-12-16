package com.github.b1412.cannon.config

import com.github.b1412.cannon.dao.base.BaseDaoImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "customDateTimeProvider")
@EnableJpaRepositories(basePackages = ["com.github.b1412.cannon.dao"], repositoryBaseClass = BaseDaoImpl::class)
@EnableTransactionManagement
class JpaConfig
