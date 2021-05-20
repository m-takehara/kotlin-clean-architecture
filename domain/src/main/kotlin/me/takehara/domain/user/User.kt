package me.takehara.domain.user

import me.takehara.domain.DateTime

data class UserId(val value: String)
data class UserName(val value: String)
data class LoginId(val value: String)
data class LoginPassword(val value: String)
data class MailAddress(val value: String)

data class UserProfile(
    val userId: UserId,
    val registeredAt: DateTime,
    val name: UserName,
    val mailAddress: MailAddress
)
