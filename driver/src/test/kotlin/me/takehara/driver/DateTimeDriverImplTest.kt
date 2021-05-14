package me.takehara.driver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.OffsetDateTime

class DateTimeDriverImplTest : FunSpec({
    val target = DateTimeDriverImpl()

    afterTest {
        unmockkAll()
    }

    test("現在時刻を取得できる") {
        val now = mockk<OffsetDateTime>()

        mockkStatic(OffsetDateTime::class.qualifiedName!!)
        every { OffsetDateTime.now() } returns now

        val actual = target.getNow()

        actual shouldBe now
        verify { OffsetDateTime.now() }
    }
})
