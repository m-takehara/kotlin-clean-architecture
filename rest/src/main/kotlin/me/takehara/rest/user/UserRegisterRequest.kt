package me.takehara.rest.user

import kotlinx.serialization.Serializable
import me.takehara.domain.user.LoginId
import me.takehara.domain.user.MailAddress
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

@Serializable
data class MailAddressAlreadyUsedResponse(val error: String) {
    constructor(mailAddress: MailAddress) : this(
        "Email address is already used: ${mailAddress.value}"
    )
}

@Serializable
data class LoginIdAlreadyUsedResponse(val error: String) {
    constructor(loginId: LoginId) : this(
        "Login ID is already used: ${loginId.value}"
    )
}
