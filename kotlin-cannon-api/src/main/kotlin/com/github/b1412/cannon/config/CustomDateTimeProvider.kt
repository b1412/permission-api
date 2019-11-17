package com.github.b1412.cannon.config

import org.springframework.data.auditing.DateTimeProvider
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor
import java.util.*

@Component
class CustomDateTimeProvider : DateTimeProvider {
    override fun getNow(): Optional<TemporalAccessor> {
        return Optional.of(ZonedDateTime.now())
    }
}