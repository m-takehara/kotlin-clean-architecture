package me.takehara.driver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.unmockkAll

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
})
