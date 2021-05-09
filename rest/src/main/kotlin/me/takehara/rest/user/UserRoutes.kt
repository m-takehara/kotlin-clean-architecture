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
            val id = call.parameters["id"]?.let(::UserId) ?: return@get call.respond("ID not found")

            val userProfile = usecase.findUserProfile(id)

            call.response.status(HttpStatusCode.OK)
            call.respond(UserProfileResponse(userProfile))
        }
        post {
            val (name, mailAddress, password) = call.receive<UserRegisterRequest>()

            // TODO: パスワードをどのタイミングでハッシュ化するか考える
            val userId = usecase.registerUser(UserName(name), MailAddress(mailAddress), LoginPassword(password))

            call.response.status(HttpStatusCode.Created)
            call.respond(UserRegisterResponse(userId))
        }
    }
}
