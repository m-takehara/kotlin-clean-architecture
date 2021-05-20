package me.takehara.usecase.user

import me.takehara.domain.user.*
import me.takehara.port.user.UserPort

class UserUsecase(private val userPort: UserPort) {
    fun registerUser(
        userName: UserName,
        mailAddress: MailAddress,
        loginPassword: LoginPassword
    ): UserId = userPort.createTransaction {
        val id = userPort.registerUser()
        val loginId = LoginId(mailAddress.value)
        userPort.registerUserProfile(id, userName, mailAddress)
        userPort.registerUserAuth(id, loginId, loginPassword)
        return@createTransaction id
    }

    fun findUserProfile(id: UserId): UserProfile = userPort.createTransaction {
        return@createTransaction userPort.findUserProfile(id)
    }
}
