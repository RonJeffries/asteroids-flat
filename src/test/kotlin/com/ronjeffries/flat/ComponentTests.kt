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
    fun `action timer does not tick on inactive entity`() {
        val entity = newAsteroid()
        entity.active = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, true) { executed = true}
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be unchanged")
            .isEqualTo(timerTime)
    }

    @Test
    fun `action timer ticks on active entity`() {
        val entity = newAsteroid()
        entity.active = true
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, true) { executed = true}
        update(timer,0.5)
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
        val timer = Timer(entity, timerTime, true) { executed = true}
        update(timer,1.0)
        assertThat(executed)
            .isEqualTo(true)
            .describedAs("action should be taken")
    }

    @Test
    fun `action timer resets time on time elapsed`() {
        val entity = newAsteroid()
        entity.active = true
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, true) { executed = true}
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

    @Test
    fun `idle timer can be created`() {
        val entity = newAsteroid()
        val timer = Timer(entity, 1.0, false) {}
        assertThat(timer).isNotEqualTo(null)
    }

    @Test
    fun `idle timer does not tick on active entity`() {
        val entity = newAsteroid()
        entity.active = true
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, false) { }
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be unchanged")
            .isEqualTo(timerTime)
    }

    @Test
    fun `idle timer does tick on inactive entity`() {
        val entity = newAsteroid()
        entity.active = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, false) { }
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
    }

    @Test
    fun `idle timer resets time when time elapsed`() {
        val entity = newAsteroid()
        entity.active = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, false) { }
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be changed")
            .isEqualTo(timerTime-0.5, within(0.01))
        update(timer,0.5)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

    @Test
    fun `idle timer takes action on inactive entity when time elapsed`() {
        val entity = newAsteroid()
        entity.active = false
        executed = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, false) { executed = true}
        update(timer,1.1)
        assertThat(executed)
            .describedAs("action should be taken")
            .isEqualTo(true)
    }

    @Test
    fun `idle timer triggers and resets on time going negative`() {
        val entity = newAsteroid()
        entity.active = false
        executed = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, false) { executed = true}
        update(timer,1.1)
        assertThat(executed)
            .describedAs("action should be taken")
            .isEqualTo(true)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

    @Test
    fun `action timer triggers and resets on time going negative`() {
        val entity = newAsteroid()
        entity.active = true
        executed = false
        val timerTime = 1.0
        val timer = Timer(entity, timerTime, true) { executed = true}
        update(timer,1.1)
        assertThat(executed)
            .describedAs("action should be taken")
            .isEqualTo(true)
        assertThat(timer.time)
            .describedAs("should be reset")
            .isEqualTo(timerTime)
    }

    @Test
    fun `timer resets on deactivate`() {
        val missile = newMissile()
        missile.active = true
        val timer = missile.components.find { it is Timer }!! as Timer
        val originalTime = timer.time
        update(timer, 0.5)
        assertThat(timer.time).describedAs("didn't tick down").isEqualTo(originalTime - 0.5, within(0.01))
        deactivate(missile)
        assertThat(timer.time).describedAs("didn't reset").isEqualTo(originalTime)
    }

}