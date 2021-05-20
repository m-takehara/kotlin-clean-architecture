package me.takehara.rest.user

import kotlinx.serialization.Serializable
import me.takehara.domain.user.UserId
import me.takehara.domain.user.UserProfile

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val mailAddress: String
) {
    constructor(profile: UserProfile) : this(
        profile.userId.value,
        profile.name.value,
        profile.mailAddress.value
    )
}

@Serializable
data class UserIdRequiredResponse(
    val error: String = "User ID must be specified."
)

@Serializable
data class UserNotFoundResponse(val error: String) {
    companion object {
        fun of(id: UserId): UserNotFoundResponse {
            return UserNotFoundResponse("User not found: ${id.value}")
        }
    }
}
