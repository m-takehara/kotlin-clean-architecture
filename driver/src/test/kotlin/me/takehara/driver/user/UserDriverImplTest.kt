package me.takehara.driver.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.takehara.domain.DateTime
import me.takehara.domain.user.UserId
import org.h2.tools.Server
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class UserDriverImplTest : FunSpec({
    var server: Server? = null
    var database: Database? = null
    var target: UserDriverImpl? = null
    beforeSpec {
        Class.forName("org.h2.Driver")
        server = Server.createTcpServer("-tcpAllowOthers").start()
        database = Database.connect(
            url = "jdbc:h2:mem:user_driver_impl_test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )
        target = UserDriverImpl(database!!)
    }
    afterSpec {
        server?.stop()
    }

    test("ユーザのプロフィールを取得できる") {
        val dateTime = LocalDateTime.parse("2021-05-18T23:30:45.334")
        val expected = OffsetDateTime.parse("2021-05-18T23:30:45.334Z")
        insertTestData(database!!, "id", dateTime, "name", "mailAddress")

        val profile = transaction(database!!) {
            target!!.findUserProfile(UserId("id"))
        }

        profile.userId.value shouldBe "id"
        profile.name.value shouldBe "name"
        profile.mailAddress.value shouldBe "mailAddress"
        profile.registeredAt.value shouldBe expected
        profile.registeredAt.value.offset shouldBe ZoneOffset.UTC
    }

    test("Userテーブルにレコードを新規作成できる") {
        val userId = UserId("id")
        val registeredAt = DateTime(OffsetDateTime.parse("2021-05-20T12:01:30.230Z"))

        transaction(database!!) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createSchema(Schema("demo_schema"))
            SchemaUtils.create(Users)
            SchemaUtils.create(UserProfiles)
            target!!.registerUser(userId, registeredAt)
            val actual = Users.select { Users.id eq userId.value }.toList()
            actual.size shouldBe 1
            actual[0][Users.id] shouldBe userId.value
            actual[0][Users.registeredAt] shouldBe registeredAt.value.toLocalDateTime()
        }
    }
})

private fun insertTestData(
    database: Database,
    id: String,
    registeredAt: LocalDateTime,
    name: String,
    mailAddress: String
) {
    transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.createSchema(Schema("demo_schema"))
        SchemaUtils.create(Users)
        SchemaUtils.create(UserProfiles)
        Users.insert {
            it[Users.id] = id
            it[Users.registeredAt] = registeredAt
        }
        UserProfiles.insert {
            it[userId] = id
            it[UserProfiles.name] = name
            it[UserProfiles.mailAddress] = mailAddress
        }
    }
}
