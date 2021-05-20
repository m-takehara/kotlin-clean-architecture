package me.takehara.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import me.takehara.driver.UuidDriverImpl
import me.takehara.driver.user.UserDriverImpl
import me.takehara.gateway.UuidDriver
import me.takehara.gateway.user.UserDriver
import me.takehara.gateway.user.UserGateway
import me.takehara.port.user.UserPort
import me.takehara.rest.user.registerUserRoutes
import me.takehara.usecase.user.UserUsecase
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    install(Koin) {
        slf4jLogger()
        modules(commonModules, userModules)
    }
    registerUserRoutes()
}

val commonModules = module(createdAtStart = true) {
    singleBy<UuidDriver, UuidDriverImpl>()
}

val userModules = module(createdAtStart = true) {
    single<UserUsecase>()
    singleBy<UserPort, UserGateway>()
    singleBy<UserDriver, UserDriverImpl>()
}
