package me.takehara.usecase.user

import me.takehara.domain.user.*
import me.takehara.port.user.UserPort

class UserUsecase(private val userPort: UserPort) {
    fun registerUser(
        userName: UserName,
        mailAddress: MailAddress,
        rawLoginPassword: RawLoginPassword
    ): UserId = userPort.createTransaction {
        val loginId = LoginId(mailAddress.value)
        val password = rawLoginPassword.encrypt()

        val id = userPort.registerUser()
        userPort.registerUserProfile(id, userName, mailAddress)
        userPort.registerUserAuth(id, loginId, password)
        return@createTransaction id
    }

    fun findUserProfile(id: UserId): UserProfile = userPort.createTransaction {
        return@createTransaction userPort.findUserProfile(id)
    }
}
