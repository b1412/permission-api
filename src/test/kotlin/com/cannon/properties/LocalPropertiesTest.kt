package com.cannon.properties

import com.cannon.config.CustomDateTimeProvider
import com.cannon.config.JpaConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.core.env.Environment
import org.springframework.data.auditing.config.AuditingConfiguration
import org.springframework.test.context.ActiveProfiles


@DataJpaTest(
    includeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [AuditingConfiguration::class, JpaConfig::class, CustomDateTimeProvider::class]
    )]
)
@ActiveProfiles("local")
class LocalPropertiesTest {
    @Autowired
    private val environment: Environment? = null

    @Test
    fun `pass the test when the active profile is local`() {
        val activeProfile = environment!!.activeProfiles.first()
        assertThat(activeProfile).isEqualTo("local")
    }

}