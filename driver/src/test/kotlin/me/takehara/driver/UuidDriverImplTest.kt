package me.takehara.driver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.util.*

class UuidDriverImplTest : FunSpec({
    val target = UuidDriverImpl()

    afterTest {
        unmockkAll()
    }

    test("値の異なるUUIDを連続で生成できる") {
        val v1 = target.generate()
        val v2 = target.generate()
        val v3 = target.generate()

        v1 shouldNotBe v2
        v1 shouldNotBe v3
        v2 shouldNotBe v3
    }

    test("コンストラクタを与えると、そのコンストラクタを使ってインスタンスを生成できる") {
        val constructor = { str: String -> listOf(str) }

        mockkStatic(UUID::class.qualifiedName!!)
        every { UUID.randomUUID().toString() } returns "random"

        val expected = listOf("random")
        val actual = target.generate(constructor)

        actual shouldBe expected
    }
})
