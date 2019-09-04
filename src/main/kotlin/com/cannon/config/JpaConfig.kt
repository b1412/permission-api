package com.cannon.config

import com.cannon.dao.base.BaseDaoImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "customDateTimeProvider")
@EnableJpaRepositories(basePackages = ["com.cannon.dao"], repositoryBaseClass = BaseDaoImpl::class)
@EnableTransactionManagement
class JpaConfig
