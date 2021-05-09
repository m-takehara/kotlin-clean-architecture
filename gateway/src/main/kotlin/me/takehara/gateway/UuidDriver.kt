package me.takehara.gateway

interface UuidDriver {
    fun generate(): String
    fun <T> generate(constructor: (String) -> T): T
}
