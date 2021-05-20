package me.takehara.domain

import java.time.OffsetDateTime

data class DateTime(val value: OffsetDateTime) {
    companion object {
        fun getNow() = DateTime(OffsetDateTime.now())
    }
}
