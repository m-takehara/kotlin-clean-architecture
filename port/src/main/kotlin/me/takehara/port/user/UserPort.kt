package me.takehara.port.user

import me.takehara.domain.user.*

interface UserPort {
    fun registerUser(
        userName: UserName,
        mailAddress: MailAddress,
        loginId: LoginId,
        loginPassword: LoginPassword
    ): UserId

    fun findUserProfile(id: UserId): UserProfile
}
