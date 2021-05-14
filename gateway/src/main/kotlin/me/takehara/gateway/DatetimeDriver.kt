package me.takehara.gateway

import java.time.OffsetDateTime

interface DatetimeDriver {
    fun getNow(): OffsetDateTime
}
