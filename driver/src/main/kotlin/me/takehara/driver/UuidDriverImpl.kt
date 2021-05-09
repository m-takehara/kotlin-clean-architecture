package me.takehara.driver

import me.takehara.gateway.UuidDriver
import java.util.*

class UuidDriverImpl : UuidDriver {
    override fun generate(): String = UUID.randomUUID().toString()
    override fun <T> generate(constructor: (String) -> T): T = constructor(generate())
}
