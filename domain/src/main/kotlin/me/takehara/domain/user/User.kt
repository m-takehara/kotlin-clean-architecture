package me.takehara.domain.user

import me.takehara.domain.DateTime
import me.takehara.domain.Identifiable
import java.security.MessageDigest

data class UserId(override val value: String) : Identifiable
data class UserName(val value: String)
data class MailAddress(val value: String)
data class LoginId(override val value: String) : Identifiable
data class EncryptedLoginPassword(val value: String)
data class RawLoginPassword(val value: String) {
    fun encrypt(): EncryptedLoginPassword {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
            .let(::EncryptedLoginPassword)
    }
}

data class UserProfile(
    val userId: UserId,
    val registeredAt: DateTime,
    val name: UserName,
    val mailAddress: MailAddress
)
