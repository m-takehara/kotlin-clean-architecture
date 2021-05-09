package me.takehara.gateway.user

import me.takehara.domain.user.*
import me.takehara.gateway.DatetimeDriver
import me.takehara.gateway.UuidDriver
import me.takehara.port.user.UserPort

class UserGateway(
    private val userDriver: UserDriver,
    private val uuidDriver: UuidDriver,
    private val datetimeDriver: DatetimeDriver
) : UserPort {
    override fun registerUser(
        userName: UserName,
        mailAddress: MailAddress,
        loginId: LoginId,
        loginPassword: LoginPassword
    ): UserId {
        val userId = uuidDriver.generate(::UserId)
        val createdAt = datetimeDriver.getNow(::CreatedAt)
        userDriver.registerUser(userId, createdAt)
        userDriver.registerUserProfile(userId, createdAt, userName, mailAddress)
        userDriver.registerUserAuth(userId, createdAt, loginId, loginPassword)
        return userId
    }

    override fun findUserProfile(id: UserId): UserProfile {
        return userDriver.findUserProfile(id)
    }
}
