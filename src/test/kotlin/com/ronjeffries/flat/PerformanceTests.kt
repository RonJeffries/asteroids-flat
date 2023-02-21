package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.system.measureNanoTime

var size = 5000
var numbers = Array<Int>(size) { it }
val iterations = 1000

class PerformanceTests {
    @BeforeEach
    fun `set up array`() {
        numbers = Array<Int>(size) { it }
    }

    @Test
    fun `distant access`() {
        val second = size - 1
        val action = {
            for (i in 0..iterations) {
                numbers[0] = numbers[second] + 1
                numbers[second] = numbers[0] + 1
            }
        }
        val nano = measureNanoTime(action)
        println("distant $nano")
    }

    @Test
    fun `local access`() {
        val second = 1
        val action = {
            for (i in 0..iterations) {
                numbers[0] = numbers[second] + 1
                numbers[second] = numbers[0] + 1
            }
        }
        val nano = measureNanoTime(action)
        println("local $nano")
    }
}