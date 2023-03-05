package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class ComponentTests {
    @Test
    fun `saucer timer activates after SaucerDelay`() {
        val saucer = newSaucer()
        val timer = SaucerTimer(saucer)
        assertThat(saucer.active)
            .describedAs("should start inactive").isEqualTo(false)
        updateSaucerTimer(timer, 0.5)
        assertThat(saucer.active)
            .describedAs("should not activate immediately").isEqualTo(false)
        updateSaucerTimer(timer, U.SaucerDelay)
        assertThat(saucer.active)
            .describedAs("should start by now").isEqualTo(true)
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

    var executed = false
    @Test
    fun `pluggable timer triggers`() {
        val entity = newAsteroid()
        val timer = PluggableTimer(entity, 5.0) {timer: PluggableTimer -> executed = true}
        executed = false
        timer.update(4.0)
        assertThat(executed)
            .describedAs("not time yet")
            .isEqualTo(false)
        timer.update(1.0)
        assertThat(executed)
            .describedAs("should have triggered")
            .isEqualTo(true)
        assertThat(timer.time)
            .describedAs("should reset timer")
            .isEqualTo(5.0)
    }

    @Test
    fun `action timer does not tick on inactive entity`() {
        val entity = newAsteroid()
        assertThat(entity.active)
            .describedAs("initialized to false")
            .isEqualTo(false)
        executed = false
        val timerTime = 1.0
        val timer = ActionTimer(entity, timerTime) { executed = true}
        timer.update(0.5)
        assertThat(timer.time).isEqualTo(timerTime)
            .describedAs("should be unchanged")
            .isEqualTo(timerTime)
    }
}