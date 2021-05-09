package me.takehara.domain.user

import java.time.OffsetDateTime

data class CreatedAt(override val value: OffsetDateTime) : Datetime
data class UpdatedAt(override val value: OffsetDateTime) : Datetime

interface Datetime {
    val value: OffsetDateTime
    fun compareTo(other: Datetime): Int {
        return value.compareTo(other.value)
    }
}
