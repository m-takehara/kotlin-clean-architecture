package me.takehara.gateway.user

import me.takehara.domain.DateTime
import me.takehara.domain.user.*
import me.takehara.gateway.UuidDriver
import me.takehara.port.user.UserPort

class UserGateway(
    private val userDriver: UserDriver,
    private val uuidDriver: UuidDriver
) : UserPort {
    override fun <T> createTransaction(process: () -> T): T = userDriver.createTransaction(process)

    override fun findUserProfile(id: UserId): UserProfile {
        return userDriver.findUserProfile(id)
    }

    override fun registerUser(): UserId {
        val userId = UserId(uuidDriver.generate())
        val registeredAt = DateTime.getNow()
        userDriver.registerUser(userId, registeredAt)
        return userId
    }

    override fun registerUserAuth(id: UserId, loginId: LoginId, loginPassword: EncryptedLoginPassword) {
        userDriver.registerUserAuth(id, loginId, loginPassword)
    }

    override fun registerUserProfile(id: UserId, name: UserName, mailAddress: MailAddress) {
        userDriver.registerUserProfile(id, name, mailAddress)
    }
}
