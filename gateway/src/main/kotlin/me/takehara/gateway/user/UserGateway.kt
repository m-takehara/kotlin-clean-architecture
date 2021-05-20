package me.takehara.gateway.user

import me.takehara.domain.DateTime
import me.takehara.domain.user.*
import me.takehara.gateway.UuidDriver
import me.takehara.port.user.UserNotFoundException
import me.takehara.port.user.UserPort

class UserGateway(
    private val userDriver: UserDriver,
    private val uuidDriver: UuidDriver
) : UserPort {
    override fun <T> createTransaction(process: () -> T): T = userDriver.createTransaction(process)

    override fun findUserProfile(id: UserId): UserProfile {
        return try {
            userDriver.findUserProfile(id)
        } catch (e: Exception) {
            throw UserNotFoundException("User not found: ${id.value}")
        }
    }

    override fun registerUser(): UserId {
        val userId = UserId(uuidDriver.generate())
        val registeredAt = DateTime.getNow()
        userDriver.registerUser(userId, registeredAt)
        return userId
    }

    override fun registerUserAuth(id: UserId, loginId: LoginId, loginPassword: LoginPassword) {
        // TODO: LoginId が他レコードと重複している場合は、例外を投げるようにする
        // TODO: 他レコードがすでに同一の UserId を持っている場合は、INSERT ではなく UPDATE できるようにする（register という命名も変えたい）
        userDriver.registerUserAuth(id, loginId, loginPassword)
    }

    override fun registerUserProfile(id: UserId, name: UserName, mailAddress: MailAddress) {
        // TODO: MailAddress が他レコードと重複している場合は、例外を投げるようにする
        // TODO: 他レコードがすでに同一の UserId を持っている場合は、INSERT ではなく UPDATE できるようにする（register という命名も変えたい）
        userDriver.registerUserProfile(id, name, mailAddress)
    }
}
