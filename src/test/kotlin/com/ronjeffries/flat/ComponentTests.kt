package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class ComponentTests {
    @Test
    fun `saucer timer`() {
        val saucer = newSaucer()
        saucer.position = Vector2(100.0, 100.0)
        saucer.velocity = Vector2(0.0, 0.0)
        val oldSpeed = saucerSpeed
        assertThat(saucer.active)
            .describedAs("should start inactive").isEqualTo(false)
        val timer = SaucerTimer(saucer)
        updateSaucerTimer(timer, 0.5)
        assertThat(saucer.active)
            .describedAs("should not activate immediately").isEqualTo(false)
        updateSaucerTimer(timer, U.SaucerDelay)
        assertThat(saucer.active)
            .describedAs("should start by now").isEqualTo(true)
        assertThat(saucer.x)
            .describedAs("should start on y axis").isEqualTo(0.0)
        assertThat(saucer.dx)
            .describedAs("should start at oldSpeed").isEqualTo(oldSpeed)
        assertThat(saucerSpeed)
            .describedAs("should negate saucerSpeed").isEqualTo(-oldSpeed)
    }

    @Test
    fun `saucer activates properly`() {
        val saucer = newSaucer()
        val oldSpeed = saucerSpeed
        saucer.position = Vector2(100.0,200.0)
        saucer.velocity = Vector2(999.0,999.0)
        assertThat(saucer.active)
            .describedAs("starts inactive").isEqualTo(false)
        activateSaucer(saucer)
        assertThat(saucer.active)
            .describedAs("should be active").isEqualTo(true)
        assertThat(saucer.x)
            .describedAs("should start on y axis").isEqualTo(0.0)
        assertThat(saucer.dx)
            .describedAs("speed should be oldSpeed").isEqualTo(oldSpeed)
        assertThat(saucerSpeed)
            .describedAs("should negate saucerSpeed").isEqualTo(-oldSpeed)
    }
}