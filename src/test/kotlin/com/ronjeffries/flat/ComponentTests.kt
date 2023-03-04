package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class ComponentTests {
    @Test
    fun `saucer timer`() {
        val saucer = newSaucer()
        assertThat(saucer.active).isEqualTo(false)
        val timer = SaucerTimer(saucer)
        updateSaucerTimer(timer, 0.5)
        assertThat(saucer.active).isEqualTo(false)
        updateSaucerTimer(timer, U.SaucerDelay)
        assertThat(saucer.active).describedAs("should start by now").isEqualTo(true)
    }
}