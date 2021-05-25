package me.takehara.domain.user

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.security.MessageDigest

class RawLoginPasswordTest : FunSpec({
    test("暗号化されていないパスワードを暗号化できる") {
        val rawLoginPassword = RawLoginPassword("password")

        val encrypted = MessageDigest
            .getInstance("SHA-256")
            .digest(rawLoginPassword.value.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }

        encrypted shouldBe rawLoginPassword.encrypt().value
    }
})
