package me.takehara.port.user

import me.takehara.domain.Identifiable
import me.takehara.domain.user.LoginId
import me.takehara.domain.user.MailAddress
import me.takehara.domain.user.UserId

class UniqueIndexViolationException(val id: Identifiable) : RuntimeException(id.value)
class UserNotFoundException(val userId: UserId) : RuntimeException(userId.value)
class UserIdAlreadyUsedException(val userId: UserId) : RuntimeException(userId.value)
class LoginIdAlreadyUsedException(val loginId: LoginId) : RuntimeException(loginId.value)
class MailAddressAlreadyUsedException(val mailAddress: MailAddress) : RuntimeException(mailAddress.value)
