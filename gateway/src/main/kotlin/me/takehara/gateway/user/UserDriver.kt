package me.takehara.gateway.user

import me.takehara.domain.user.*

interface UserDriver {
    fun findUserProfile(id: UserId): UserProfile
    fun registerUser(id: UserId, createdAt: CreatedAt)
    fun registerUserAuth(userId: UserId, createdAt: CreatedAt, loginId: LoginId, loginPassword: LoginPassword)
    fun registerUserProfile(userId: UserId, createdAt: CreatedAt, name: UserName, mailAddress: MailAddress)
}
