package me.takehara.rest.user

import kotlinx.serialization.Serializable
import me.takehara.domain.user.UserId

@Serializable
data class UserRegisterRequest(
    val name: String,
    val mailAddress: String,
    val password: String
)

@Serializable
data class UserRegisterResponse(
    val id: String
) {
    constructor(id: UserId) : this(id.value)
}

@Serializable
data class BadRegistrationRequestResponse(
    val error: String = "required \"name\", \"mailAddress\" and \"password\" in request."
)
