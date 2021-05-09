package me.takehara.gateway

import java.time.OffsetDateTime

interface DatetimeDriver {
    fun getNow(): OffsetDateTime
    fun <T> getNow(constructor: (OffsetDateTime) -> T): T
}
