package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
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
        assertThat(timer.time)
            .describedAs("should be unchanged")
            .isEqualTo(timerTime)
    }

    @Test
    fun `action timer ticks on active entity`() {
        val entity = newAsteroid()
        entity.active = true
        executed = false
        val timerTime = 1.0
        val timer = ActionTimer(entity, timerTime) { executed = true}
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
    }

    @Test
    fun `action timer acts on active entity time elapsed`() {
        val entity = newAsteroid()
        entity.active = true
        executed = false
        val timerTime = 1.0
        val timer = ActionTimer(entity, timerTime) { executed = true}
        timer.update(1.0)
        assertThat(executed)
            .isEqualTo(true)
            .describedAs("action should be taken")
    }

    @Test
    fun `action timer resets time on time elapsed`() {
        val entity = newAsteroid()
        entity.active = true
        executed = false
        val timerTime = 1.0
        val timer = ActionTimer(entity, timerTime) { executed = true}
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

    @Test
    fun `idle timer can be created`() {
        val entity = newAsteroid()
        val timer = IdleTimer(entity, 1.0) {}
        assertThat(timer).isNotEqualTo(null)
    }

    @Test
    fun `idle timer does not tick on active entity`() {
        val entity = newAsteroid()
        entity.active = true
        val timerTime = 1.0
        val timer = IdleTimer(entity, timerTime) { }
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be unchanged")
            .isEqualTo(timerTime)
    }

    @Test
    fun `idle timer does tick on inactive entity`() {
        val entity = newAsteroid()
        entity.active = false
        val timerTime = 1.0
        val timer = IdleTimer(entity, timerTime) { }
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
    }

    @Test
    fun `idle timer resets time when time elapsed`() {
        val entity = newAsteroid()
        entity.active = false
        val timerTime = 1.0
        val timer = IdleTimer(entity, timerTime) { }
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
        timer.update(0.5)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

}