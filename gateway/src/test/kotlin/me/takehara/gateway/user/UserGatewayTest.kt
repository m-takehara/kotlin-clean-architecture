package me.takehara.gateway.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import me.takehara.domain.DateTime
import me.takehara.domain.user.*
import me.takehara.gateway.UuidDriver

class UserGatewayTest : FunSpec({
    val userDriver = mockk<UserDriver>()
    val uuidDriver = mockk<UuidDriver>()
    val target = UserGateway(userDriver, uuidDriver)

    finalizeSpec {
        unmockkAll()
    }

    test("createTransaction: トランザクションを作成できる") {
        val mockk = mockk<Any>()
        val process = { mockk }
        every { userDriver.createTransaction(process) } returns mockk

        target.createTransaction(process)

        verify { userDriver.createTransaction(process) }
    }

    test("findUserProfile: 指定した UserId を含むプロファイルを検索できる") {
        val userId = mockk<UserId>()
        val profile = mockk<UserProfile>()
        every { userDriver.findUserProfile(userId) } returns profile

        val actual = target.findUserProfile(userId)

        actual shouldBe profile
        verify { userDriver.findUserProfile(userId) }
    }

    test("registerUser: Users テーブルにレコードを追加し、追加したレコードの UserId を返す") {
        val id = UserId("uuid")
        val dateTime = DateTime(mockk())

        mockkObject(DateTime.Companion)
        every { DateTime.getNow() } returns dateTime
        every { uuidDriver.generate() } returns id.value
        every { userDriver.registerUser(id, dateTime) } just runs

        val actual = target.registerUser()

        actual shouldBe id
        verify { DateTime.getNow() }
        verify { uuidDriver.generate() }
        verify { userDriver.registerUser(id, dateTime) }
    }

    test("registerUserAuth: UserAuths テーブルにレコードを追加できる") {
        val userId = mockk<UserId>()
        val loginId = mockk<LoginId>()
        val loginPassword = mockk<EncryptedLoginPassword>()

        every { userDriver.registerUserAuth(userId, loginId, loginPassword) } just runs

        target.registerUserAuth(userId, loginId, loginPassword)

        verify { userDriver.registerUserAuth(userId, loginId, loginPassword) }
    }

    test("registerUserProfile: UserProfiles テーブルにレコードを追加できる") {
        val userId = mockk<UserId>()
        val name = mockk<UserName>()
        val password = mockk<MailAddress>()

        every { userDriver.registerUserProfile(userId, name, password) } just runs

        target.registerUserProfile(userId, name, password)

        verify { userDriver.registerUserProfile(userId, name, password) }
    }
})
