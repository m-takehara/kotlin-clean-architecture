package me.takehara.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.time.OffsetDateTime

class DateTimeTest : FunSpec({
    test("現在時刻を持つ DateTime インスタンスを生成できる") {
        val dateTime = mockk<OffsetDateTime>()
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns dateTime

        val actual = DateTime.getNow()
        actual shouldBe DateTime(dateTime)
        actual.value shouldBe dateTime
    }
})
