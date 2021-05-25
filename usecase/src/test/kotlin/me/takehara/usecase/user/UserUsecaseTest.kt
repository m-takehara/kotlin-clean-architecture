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
        // NOTE: LoginId のコンストラクタをモックすることができないため mockk<LoginId> を使っていない
        val loginId = LoginId("mailAddress")
        val rawLoginPassword = mockk<RawLoginPassword>()
        val encryptedPassword = mockk<EncryptedLoginPassword>()

        every { userPort.createTransaction(any<() -> UserId>()) } answers { firstArg<() -> UserId>()() }

        every { mailAddress.value } returns loginId.value
        every { rawLoginPassword.encrypt() } returns encryptedPassword
        every { userPort.registerUser() } returns userId
        every { userPort.registerUserProfile(userId, userName, mailAddress) } just runs
        every { userPort.registerUserAuth(userId, loginId, encryptedPassword) } just runs

        val actual = target.registerUser(userName, mailAddress, rawLoginPassword)

        actual shouldBe userId
        verify { userPort.createTransaction(any<() -> UserId>()) }
        verify { mailAddress.value }
        verify { rawLoginPassword.encrypt() }
        verify { userPort.registerUser() }
        verify { userPort.registerUserProfile(userId, userName, mailAddress) }
        verify { userPort.registerUserAuth(userId, loginId, encryptedPassword) }
    }

    test("ユーザIDを与えると、そのユーザIDを含むユーザプロファイルを得られる") {
        val id = mockk<UserId>()
        val profile = mockk<UserProfile>()

        every { userPort.createTransaction(any<() -> UserProfile>()) } answers { firstArg<() -> UserProfile>()() }
        every { userPort.findUserProfile(id) } returns profile

        val actual = target.findUserProfile(id)

        actual shouldBe profile
        verify { userPort.createTransaction(any<() -> Unit>()) }
        verify { userPort.findUserProfile(id) }
    }
})
