package me.takehara.driver.user

import me.takehara.domain.DateTime
import me.takehara.domain.user.*
import me.takehara.gateway.user.UserDriver
import me.takehara.port.user.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset

class UserDriverImpl(
    private val database: Database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/demo_db",
        driver = "org.postgresql.Driver",
        user = "demo",
        password = "demo"
    )
) : UserDriver {
    override fun findUserProfile(id: UserId): UserProfile {
        val user = Users.select { Users.id eq id.value }
            .also { if (!it.any()) throw UserNotFoundException(id) }.single()
        val profile = UserProfiles.select { UserProfiles.userId eq id.value }
            .also { if (!it.any()) throw UserNotFoundException(id) }.single()

        return UserProfile(
            profile[UserProfiles.userId].let(::UserId),
            user[Users.registeredAt].let { OffsetDateTime.of(it, ZoneOffset.UTC) }.let(::DateTime),
            profile[UserProfiles.name].let(::UserName),
            profile[UserProfiles.mailAddress].let(::MailAddress)
        )
    }

    override fun registerUser(id: UserId, registeredAt: DateTime) {
        if (Users.select { Users.id eq id.value }.any())
            throw UserIdAlreadyUsedException(id)
        Users.insert {
            it[Users.id] = id.value
            it[Users.registeredAt] = registeredAt.value.toLocalDateTime()
        }
    }

    override fun registerUserAuth(
        userId: UserId,
        loginId: LoginId,
        loginPassword: EncryptedLoginPassword
    ) {
        if (UserAuths.select { UserAuths.userId eq userId.value }.any())
            throw UniqueIndexViolationException(userId)
        if (UserAuths.select { UserAuths.loginId eq loginId.value }.any())
            throw LoginIdAlreadyUsedException(loginId)
        UserAuths.insert {
            it[UserAuths.userId] = userId.value
            it[UserAuths.loginId] = loginId.value
            it[UserAuths.loginPassword] = loginPassword.value
        }
    }

    override fun registerUserProfile(userId: UserId, name: UserName, mailAddress: MailAddress) {
        if (UserProfiles.select { UserProfiles.userId eq userId.value }.any())
            throw UniqueIndexViolationException(userId)
        if (UserProfiles.select { UserProfiles.mailAddress eq mailAddress.value }.any())
            throw MailAddressAlreadyUsedException(mailAddress)
        UserProfiles.insert {
            it[UserProfiles.userId] = userId.value
            it[UserProfiles.name] = name.value
            it[UserProfiles.mailAddress] = mailAddress.value
        }
    }

    override fun <T> createTransaction(process: () -> T): T = transaction {
        addLogger(StdOutSqlLogger)
        process()
    }
}

object Users : Table("demo_schema.users") {
    val id = varchar("id", 36)
    val registeredAt = datetime("registered_at")

    override val primaryKey = PrimaryKey(id)
}

object UserProfiles : Table("demo_schema.user_profiles") {
    val userId = varchar("user_id", 36) references Users.id
    val name = varchar("name", 256)
    val mailAddress = varchar("mail_address", 256)

    override val primaryKey = PrimaryKey(userId)
}

object UserAuths : Table("demo_schema.user_auths") {
    val userId = varchar("user_id", 36) references Users.id
    val loginId = varchar("login_id", 256)
    val loginPassword = varchar("login_password", 256)

    override val primaryKey = PrimaryKey(userId)
}
