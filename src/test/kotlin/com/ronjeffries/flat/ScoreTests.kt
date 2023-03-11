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
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(100.0, 100.0)
        missile.active = true
        val oldScore = Score
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(asteroid.scale).isEqualTo(2.0)
        assertThat(Score - oldScore).isEqualTo(20)
    }

    @Test
    fun `scale 2 asteroid increases score by 50`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 2.0
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(100.0, 100.0)
        missile.active = true
        val oldScore = Score
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(asteroid.scale).isEqualTo(1.0)
        assertThat(Score - oldScore).isEqualTo(50)
    }

    @Test
    fun `scale 1 asteroid increases score by 100`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 1.0
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(100.0, 100.0)
        missile.active = true
        val oldScore = Score
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(asteroid.active).isEqualTo(false)
        assertThat(Score - oldScore).isEqualTo(100)
    }

    @Test
    fun `saucer increases score by 200`() {
        val saucer = newSaucer()
        saucer.position = Vector2(100.0, 100.0)
        saucer.active = true
        saucer.scale = 1.0
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        val oldScore = Score
        checkCollisionWithScore(saucer, missile, U.SaucerScore)
        assertThat(Score - oldScore).isEqualTo(200)
        assertThat(saucer.active).isEqualTo(false)
    }

    @Test
    fun `kill radius tests`() {
        val missile = newMissile()
        val missileRad = killRadius(missile)
        assertThat(missileRad).isEqualTo(U.MissileKillRadius)
    }

    @Test
    fun `asteroid radii`() {
        val asteroid = newAsteroid()
        assertThat(killRadius(asteroid)).isEqualTo(U.AsteroidKillRadius * 4.0)
        asteroid.scale = 1.0
        assertThat(killRadius(asteroid)).isEqualTo(U.AsteroidKillRadius)
    }

    @Test
    fun `ship radius`() {
        val ship = newShip()
        assertThat(killRadius(ship)).isEqualTo(U.ShipKillRadius)
    }

    @Test
    fun `saucer radius`() {
        val saucer = newSaucer()
        assertThat(killRadius(saucer)).isEqualTo(U.SaucerKilLRadius)
    }

}
