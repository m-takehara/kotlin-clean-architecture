package me.takehara.driver

import me.takehara.gateway.UuidDriver
import java.util.*

class UuidDriverImpl : UuidDriver {
    override fun generate(): String = UUID.randomUUID().toString()
}
