package me.takehara.driver

import me.takehara.gateway.DatetimeDriver
import java.time.OffsetDateTime

class DateTimeDriverImpl : DatetimeDriver {
    override fun getNow(): OffsetDateTime = OffsetDateTime.now()!!
}
