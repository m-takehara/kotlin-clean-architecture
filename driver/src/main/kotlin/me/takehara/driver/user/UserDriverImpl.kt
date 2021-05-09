package me.takehara.driver.user

import me.takehara.domain.user.*
import me.takehara.gateway.user.UserDriver
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset

class UserDriverImpl : UserDriver {
    private var database: Database

    init {
        this.database = Database.connect(
            url = "jdbc:postgresql://localhost:5432/demo_db",
            driver = "org.postgresql.Driver",
            user = "demo",
            password = "demo"
        )
    }

    override fun findUserProfile(id: UserId): UserProfile {
        return transaction {
            val profile = UserProfiles.select { UserProfiles.userId eq id.value }.single()
            UserProfile(
                profile[UserProfiles.userId].let(::UserId),
                profile[UserProfiles.name].let(::UserName),
                profile[UserProfiles.mailAddress].let(::MailAddress),
                // NOTE: 日付データを DB に入れるときはすべて UTC で入れる前提なので ZoneOffset.UTC 決め打ち
                profile[UserProfiles.createdAt].let { OffsetDateTime.of(it, ZoneOffset.UTC) }.let(::CreatedAt),
                profile[UserProfiles.updatedAt]?.let { OffsetDateTime.of(it, ZoneOffset.UTC) }?.let(::UpdatedAt)
            )
        }
    }

    override fun registerUser(id: UserId, createdAt: CreatedAt) {
        transaction {
            Users.insert {
                it[Users.id] = id.value
                it[Users.createdAt] = createdAt.value.toLocalDateTime()
            }
        }
    }

    override fun registerUserAuth(
        userId: UserId,
        createdAt: CreatedAt,
        loginId: LoginId,
        loginPassword: LoginPassword
    ) {
        transaction {
            UserAuths.insert {
                it[UserAuths.userId] = userId.value
                it[UserAuths.loginId] = loginId.value
                it[UserAuths.loginPassword] = loginPassword.value
                it[UserAuths.createdAt] = createdAt.value.toLocalDateTime()
            }
        }
    }

    override fun registerUserProfile(userId: UserId, createdAt: CreatedAt, name: UserName, mailAddress: MailAddress) {
        transaction {
            UserProfiles.insert {
                it[UserProfiles.userId] = userId.value
                it[UserProfiles.name] = name.value
                it[UserProfiles.mailAddress] = mailAddress.value
                it[UserProfiles.createdAt] = createdAt.value.toLocalDateTime()
            }
        }
    }
}

object Users : Table("demo_schema.users") {
    val id = varchar("id", 36)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

object UserProfiles : Table("demo_schema.user_profiles") {
    val userId = varchar("user_id", 36) references Users.id
    val name = varchar("name", 256)
    val mailAddress = varchar("mail_address", 256)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(userId)
}

object UserAuths : Table("demo_schema.user_auths") {
    val userId = varchar("user_id", 36) references Users.id
    val loginId = varchar("login_id", 256)
    val loginPassword = varchar("login_password", 256)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(userId)
}
