package me.takehara.driver

import me.takehara.gateway.DatetimeDriver
import java.time.OffsetDateTime
import java.time.ZonedDateTime

class DateTimeDriverImpl : DatetimeDriver {
    override fun getNow(): OffsetDateTime = OffsetDateTime.now()!!
    override fun <T> getNow(constructor: (OffsetDateTime) -> T): T = constructor(getNow())
}
