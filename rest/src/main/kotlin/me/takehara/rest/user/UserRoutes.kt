package me.takehara.rest.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import me.takehara.domain.user.LoginPassword
import me.takehara.domain.user.MailAddress
import me.takehara.domain.user.UserId
import me.takehara.domain.user.UserName
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

            // TODO: パスワードをどのタイミングでハッシュ化するか考える
            val userId = usecase.registerUser(UserName(name), MailAddress(mailAddress), LoginPassword(password))

            call.response.status(HttpStatusCode.Created)
            call.respond(UserRegisterResponse(userId))
        }
    }
}
