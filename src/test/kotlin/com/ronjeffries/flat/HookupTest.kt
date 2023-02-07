package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test

class HookupTest {
    @Test
    fun `hook up`() {
        assertThat(1+1).isEqualTo(2)
    }

    @Test
    fun `move an asteroid`() {
        val asteroid = Asteroid(100.0, 100.0, 10.0, -10.0)
        moveAsteroid(asteroid, 200.0, 200.0, 0.5)
        assertThat(asteroid.x).isEqualTo(105.0, within(0.01))
        assertThat(asteroid.y).isEqualTo(95.0, within(0.01))
    }

    @Test
    fun `asteroid wraps high`() {
        val asteroid = Asteroid(95.0, 98.0, 10.0, 10.0)
        moveAsteroid(asteroid, 100.0, 100.0, 1.0)
        assertThat(asteroid.x).isEqualTo(5.0, within(0.01))
        assertThat(asteroid.y).isEqualTo(8.0, within(0.01))
    }

    @Test
    fun `asteroid wraps low`() {
        val asteroid = Asteroid(5.0, 8.0, -10.0, -10.0)
        moveAsteroid(asteroid, 100.0, 100.0, 1.0)
        assertThat(asteroid.x).isEqualTo(95.0, within(0.01))
        assertThat(asteroid.y).isEqualTo(93.0, within(0.01))
    }
}