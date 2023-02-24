package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class ScoreTests {
    @Test
    fun `scale 4 asteroid increases score by 20`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 4.0
        val missile :SpaceObject = newMissile()
        missile.position = Vector2(100.0,100.0)
        missile.active = true
        val oldScore = Score
        checkOneAsteroid(asteroid,missile)
        assertThat(asteroid.scale).isEqualTo(2.0)
        assertThat(Score).isEqualTo(oldScore+20)
    }
}