package me.takehara.usecase.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import me.takehara.domain.user.*
import me.takehara.port.user.UserPort

class UserUsecaseTest : FunSpec({
    val userPort = mockk<UserPort>()
    val target = UserUsecase(userPort)

    afterTest {
        unmockkAll()
    }

    test("ユーザ名・メールアドレス・パスワードを与えると、メールアドレスをログインIDとしてユーザ情報を登録できる") {
        val userId = mockk<UserId>()
        val userName = mockk<UserName>()
        val mailAddress = mockk<MailAddress>()
        val loginId = LoginId("mailAddress")
        val loginPassword = mockk<LoginPassword>()

        every { mailAddress.value } returns "mailAddress"
        every { userPort.createTransaction(any<() -> UserId>()) } answers { firstArg<() -> UserId>()() }
        every { userPort.registerUser() } returns userId
        every { userPort.registerUserProfile(userId, userName, mailAddress) } just runs
        every { userPort.registerUserAuth(userId, loginId, loginPassword) } just runs

        val actual = target.registerUser(userName, mailAddress, loginPassword)

        actual shouldBe userId
        verify { mailAddress.value }
        verify { userPort.createTransaction(any<() -> UserId>()) }
        verify { userPort.registerUser() }
        verify { userPort.registerUserProfile(userId, userName, mailAddress) }
        verify { userPort.registerUserAuth(userId, loginId, loginPassword) }
    }

    test("ユーザIDを与えると、そのユーザIDを含むユーザプロファイルを得られる") {
        val id = mockk<UserId>()
        val profile = mockk<UserProfile>()

        every { userPort.createTransaction(any<() -> UserProfile>()) } answers { firstArg<() -> UserProfile>()() }
        every { userPort.findUserProfile(id) } returns profile

        target.findUserProfile(id)

        verify { userPort.createTransaction(any<() -> Unit>()) }
        verify { userPort.findUserProfile(id) }
    }
})
