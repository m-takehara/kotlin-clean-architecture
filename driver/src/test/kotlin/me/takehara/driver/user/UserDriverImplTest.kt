package me.takehara.driver.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import me.takehara.domain.DateTime
import me.takehara.domain.user.*
import me.takehara.port.user.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class UserDriverImplTest : FunSpec({
    val database = Database.connect(
        url = "jdbc:h2:mem:user_driver_impl_test;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver",
        user = "sa",
        password = ""
    )
    val target = UserDriverImpl(database)

    beforeSpec {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createSchema(Schema("demo_schema"))
            SchemaUtils.create(Users, UserAuths, UserProfiles)
        }
    }

    beforeTest {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            UserProfiles.deleteAll()
            UserAuths.deleteAll()
            Users.deleteAll()
        }
    }

    finalizeSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("findUserProfile: 指定した ID を持つユーザのプロフィールを検索できる") {
        val dateTime = LocalDateTime.parse("2021-05-18T23:30:45.334")
        val expected = OffsetDateTime.parse("2021-05-18T23:30:45.334Z")
        insertTestData(database, registeredAt = dateTime)

        val profile = transaction(database) {
            addLogger(StdOutSqlLogger)
            target.findUserProfile(UserId("id"))
        }

        profile.userId.value shouldBe "id"
        profile.name.value shouldBe "name"
        profile.mailAddress.value shouldBe "mailAddress"
        profile.registeredAt.value shouldBe expected
        profile.registeredAt.value.offset shouldBe ZoneOffset.UTC
    }

    test("findUserProfile: 指定した ID を持つユーザが存在しない場合は例外を投げる") {
        insertTestData(database)

        shouldThrow<UserNotFoundException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.findUserProfile(UserId("foo"))
            }
        }
    }

    test("registerUser: Users テーブルにレコードを新規作成できる") {
        val userId = UserId("id")
        val registeredAt = DateTime(OffsetDateTime.parse("2021-05-20T12:01:30.230Z"))

        val actual = transaction(database) {
            addLogger(StdOutSqlLogger)
            target.registerUser(userId, registeredAt)
            Users.select { Users.id eq userId.value }.toList()
        }

        actual.size shouldBe 1
        actual[0][Users.id] shouldBe userId.value
        actual[0][Users.registeredAt] shouldBe registeredAt.value.toLocalDateTime()
    }

    test("registerUser: UserId が他レコードと重複する場合は例外を投げる") {
        insertTestData(database)

        val userId = UserId("id")
        val registeredAt = DateTime(OffsetDateTime.now())

        shouldThrow<UserIdAlreadyUsedException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.registerUser(userId, registeredAt)
            }
        }
    }

    test("registerUserAuth: UserAuths テーブルにレコードを新規作成できる") {
        val userId = UserId("id")
        val loginId = LoginId("loginId")
        val encryptedLoginPassword = EncryptedLoginPassword("password")

        val auth = transaction(database) {
            addLogger(StdOutSqlLogger)
            Users.insert {
                it[id] = "id"
                it[registeredAt] = LocalDateTime.now()
            }
            target.registerUserAuth(userId, loginId, encryptedLoginPassword)
            val toList = UserAuths.select { UserAuths.userId eq userId.value }.toList()
            toList
        }

        auth.size shouldBe 1
        auth[0][UserAuths.loginId] shouldBe loginId.value
        auth[0][UserAuths.loginPassword] shouldBe encryptedLoginPassword.value
    }

    test("registerUserAuth: UserId が他レコードと重複する場合は例外を投げる") {
        insertTestData(database)

        val userId = UserId("id")
        val loginId = LoginId(UUID.randomUUID().toString())
        val encryptedLoginPassword = EncryptedLoginPassword("password")

        shouldThrow<UniqueIndexViolationException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.registerUserAuth(userId, loginId, encryptedLoginPassword)
            }
        }
    }

    test("registerUserAuth: LoginId が他レコードと重複する場合は例外を投げる") {
        insertTestData(database)

        val userId = UserId(UUID.randomUUID().toString())
        val loginId = LoginId("loginId")
        val encryptedLoginPassword = EncryptedLoginPassword("password")

        shouldThrow<LoginIdAlreadyUsedException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.registerUserAuth(userId, loginId, encryptedLoginPassword)
            }
        }
    }

    test("registerUserProfile: UserProfiles テーブルにレコードを新規作成できる") {
        val userId = UserId("id")
        val userName = UserName("userName")
        val mailAddress = MailAddress("mailAddress")

        val profile = transaction(database) {
            addLogger(StdOutSqlLogger)
            Users.insert {
                it[id] = "id"
                it[registeredAt] = LocalDateTime.now()
            }
            target.registerUserProfile(userId, userName, mailAddress)
            UserProfiles.select { UserProfiles.userId eq userId.value }.toList()
        }

        profile.size shouldBe 1
        profile[0][UserProfiles.name] shouldBe userName.value
        profile[0][UserProfiles.mailAddress] shouldBe mailAddress.value
    }

    test("registerUserProfile: UserId が他レコードと重複する場合は例外を投げる") {
        insertTestData(database)

        val userId = UserId("id")
        val userName = UserName("name")
        val mailAddress = MailAddress(UUID.randomUUID().toString())

        shouldThrow<UniqueIndexViolationException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.registerUserProfile(userId, userName, mailAddress)
            }
        }
    }

    test("registerUserProfile: MailAddress が他レコードと重複する場合は例外を投げる") {
        insertTestData(database)

        val userId = UserId(UUID.randomUUID().toString())
        val userName = UserName("name")
        val mailAddress = MailAddress("mailAddress")

        shouldThrow<MailAddressAlreadyUsedException> {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                target.registerUserProfile(userId, userName, mailAddress)
            }
        }
    }

    test("createTransaction: トランザクション内で例外が投げられると書き込み内容がロールバックされる") {
        val runtimeException = RuntimeException("テスト用例外")
        try {
            target.createTransaction {
                Users.insert {
                    it[id] = "id"
                    it[registeredAt] = LocalDateTime.now()
                }
                Users.select { Users.id eq "id" }.any() shouldBe true

                throw runtimeException
            }
        } catch (e: Exception) {
            e shouldBeSameInstanceAs runtimeException
            transaction(database) {
                Users.select { Users.id eq "id" }.any() shouldBe false
            }
        }
    }
})

private fun insertTestData(
    database: Database,
    id: String = "id",
    registeredAt: LocalDateTime = LocalDateTime.now(),
    name: String = "name",
    mailAddress: String = "mailAddress",
    loginId: String = "loginId",
    loginPassword: String = "password"
): Unit = transaction(database) {
    addLogger(StdOutSqlLogger)
    Users.insert {
        it[Users.id] = id
        it[Users.registeredAt] = registeredAt
    }
    UserProfiles.insert {
        it[userId] = id
        it[UserProfiles.name] = name
        it[UserProfiles.mailAddress] = mailAddress
    }
    UserAuths.insert {
        it[userId] = id
        it[UserAuths.loginId] = loginId
        it[UserAuths.loginPassword] = loginPassword
    }
}
