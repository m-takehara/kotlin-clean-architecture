package me.takehara.port.user

import me.takehara.domain.user.*

interface UserPort {
    fun <T> createTransaction(process: () -> T): T

    fun findUserProfile(id: UserId): UserProfile

    fun registerUser(): UserId
    fun registerUserAuth(id: UserId, loginId: LoginId, loginPassword: LoginPassword)
    fun registerUserProfile(id: UserId, name: UserName, mailAddress: MailAddress)
}
