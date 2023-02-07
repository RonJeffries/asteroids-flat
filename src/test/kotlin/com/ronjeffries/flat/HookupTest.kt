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
        moveAsteroid(asteroid, 0.5)
        assertThat(asteroid.x).isEqualTo(105.0, within(0.01))
        assertThat(asteroid.y).isEqualTo(95.0, within(0.01))
    }
}