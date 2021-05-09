package me.takehara.usecase.user

import me.takehara.domain.user.*
import me.takehara.port.user.UserPort

class UserUsecase(private val userPort: UserPort) {
    fun registerUser(userName: UserName, mailAddress: MailAddress, loginPassword: LoginPassword): UserId {
        val loginId = LoginId(mailAddress.value)
        return userPort.registerUser(userName, mailAddress, loginId, loginPassword)
    }

    fun findUserProfile(id: UserId): UserProfile {
        return userPort.findUserProfile(id)
    }
}
