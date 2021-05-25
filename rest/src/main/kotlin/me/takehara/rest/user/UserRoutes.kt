package me.takehara.rest.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import me.takehara.domain.user.MailAddress
import me.takehara.domain.user.RawLoginPassword
import me.takehara.domain.user.UserId
import me.takehara.domain.user.UserName
import me.takehara.port.user.LoginIdAlreadyUsedException
import me.takehara.port.user.MailAddressAlreadyUsedException
import me.takehara.usecase.user.UserUsecase
import org.koin.ktor.ext.inject

fun Application.registerUserRoutes() {
    routing {
        userRouting()
    }
}

fun Route.userRouting() {
    route("/users") {
        val usecase by inject<UserUsecase>()
        get("{id}") {
            val id = call.parameters["id"]?.let(::UserId) ?: return@get run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(UserIdRequiredResponse())
            }

            kotlin.runCatching {
                usecase.findUserProfile(id)
            }.onSuccess {
                call.response.status(HttpStatusCode.OK)
                call.respond(UserProfileResponse(it))
            }.onFailure {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(UserNotFoundResponse.of(id))
            }
        }
        post {
            val (name, mailAddress, password) = try {
                call.receive<UserRegisterRequest>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(BadRegistrationRequestResponse())
                return@post
            }

            try {
                val userId = usecase.registerUser(UserName(name), MailAddress(mailAddress), RawLoginPassword(password))
                call.response.status(HttpStatusCode.Created)
                call.respond(UserRegisterResponse(userId))
            } catch (e: MailAddressAlreadyUsedException) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(MailAddressAlreadyUsedResponse(e.mailAddress))
            } catch (e: LoginIdAlreadyUsedException) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(LoginIdAlreadyUsedResponse(e.loginId))
            }
        }
    }
}
