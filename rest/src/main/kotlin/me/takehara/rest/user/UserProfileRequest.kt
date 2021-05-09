package me.takehara.rest.user

import kotlinx.serialization.Serializable
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
