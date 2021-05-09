package me.takehara.usecase.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import me.takehara.domain.user.*
import me.takehara.port.user.UserPort

class UserUsecaseTest : FunSpec({
    val userPort = mockk<UserPort>()
    val target = UserUsecase(userPort)

    afterTest {
        unmockkAll()
    }

    test("ユーザ名・メールアドレス・パスワードを与えると、メールアドレスをログインIDとしてユーザ情報を登録できる") {
        val userName = mockk<UserName>()
        val mailAddress = mockk<MailAddress>()
        val loginPassword = mockk<LoginPassword>()
        val loginId = LoginId("mailAddress")
        val expected = mockk<UserId>()

        every { mailAddress.value } returns "mailAddress"
        every { userPort.registerUser(userName, mailAddress, loginId, loginPassword) } returns expected

        val actual = target.registerUser(userName, mailAddress, loginPassword)

        actual shouldBe expected
        verify { mailAddress.value }
        verify { userPort.registerUser(userName, mailAddress, loginId, loginPassword) }
    }

    test("ユーザIDを与えると、そのユーザIDを含むユーザプロファイルを得られる") {
        val id = mockk<UserId>()
        val profile = mockk<UserProfile>()

        every { userPort.findUserProfile(id) } returns profile

        target.findUserProfile(id)

        verify { userPort.findUserProfile(id) }
    }
})
