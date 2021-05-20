package me.takehara.gateway.user

import me.takehara.domain.DateTime
import me.takehara.domain.user.*

interface UserDriver {
    fun findUserProfile(id: UserId): UserProfile

    fun registerUser(id: UserId, registeredAt: DateTime)
    fun registerUserAuth(userId: UserId, loginId: LoginId, loginPassword: LoginPassword)
    fun registerUserProfile(userId: UserId, name: UserName, mailAddress: MailAddress)

    fun <T> createTransaction(process: () -> T): T
}
