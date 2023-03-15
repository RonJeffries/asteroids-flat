package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class CollisionTests {
    @Test
    fun `asteroid and missile far apart do not collide`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(200.0, 200.0)
        assertThat(colliding(asteroid, missile)).isEqualTo(false)
    }

    @Test
    fun `asteroid and missile close enough do collide`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        assertThat(colliding(asteroid, missile)).isEqualTo(true)
    }

    @Test
    fun `when asteroid and missile collide, missile goes inactive`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 1.0
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(missile.active).isEqualTo(false)
    }

    @Test
    fun `when colliding asteroid scale is 1, it goes inactive`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 1.0
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(asteroid.active).isEqualTo(false)
    }

    @Test
    fun `when colliding asteroid scale exceeds 1, it gets smaller`() {
        createGame(4,4,23)
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 2.0
        val availableAsteroid = newAsteroid()
        SpaceObjects = arrayOf(asteroid, availableAsteroid)
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(asteroid.scale).isEqualTo(1.0)
    }

    @Test
    fun `when colliding asteroid scale exceeds 1, a new asteroid appears`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 2.0
        val availableAsteroid = newAsteroid()
        availableAsteroid.scale = 4.0
        SpaceObjects = arrayOf(asteroid, availableAsteroid)
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(availableAsteroid.scale).isEqualTo(1.0)
        assertThat(availableAsteroid.active).isEqualTo(true)
    }

    @Test
    fun `new asteroids get new velocity`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        asteroid.active = true
        asteroid.scale = 2.0
        val availableAsteroid = newAsteroid()
        availableAsteroid.scale = 4.0
        SpaceObjects = arrayOf(asteroid, availableAsteroid)
        val missile: SpaceObject = newMissile()
        missile.position = Vector2(110.0, 100.0)
        missile.active = true
        val asteroidVelocity = asteroid.velocity
        val availableAsteroidVelocity = availableAsteroid.velocity
        checkCollisionWithScore(asteroid, missile, getScore(asteroid, missile))
        assertThat(availableAsteroid.velocity).isNotEqualTo(availableAsteroidVelocity)
        assertThat(asteroid.velocity).isNotEqualTo(asteroidVelocity)
    }

    @Test
    fun `missile killing saucer activates splat`() {
        createGame(4,4,23)
        assertThat(SaucerSplat.active).isEqualTo(false)
        val missile = newMissile()
        Saucer.active = true
        missile.position = Saucer.position
        checkCollisionWithScore(Saucer, missile, 222)
        assertThat(Saucer.active).isEqualTo(false)
        assertThat(SaucerSplat.active).isEqualTo(true)
    }

    @Test
    fun `missile killing ship activates splat`() {
        createGame(4,4,23)
        assertThat(ShipSplat.active).isEqualTo(false)
        val missile = newSaucerMissile()
        Ship.active = true
        missile.position = Ship.position
        checkCollisionWithScore(Ship, missile, 222)
        assertThat(Ship.active).isEqualTo(false)
        assertThat(ShipSplat.active).isEqualTo(true)
    }

    @Test
    fun `missile killing asteroid activates splat`() {
        createGame(4,4,23)
        assertThat(AsteroidSplat.active).isEqualTo(false)
        val missile = newSaucerMissile()
        val asteroid = newAsteroid()
        missile.position = asteroid.position
        checkCollisionWithScore(asteroid, missile, 222)
        assertThat(AsteroidSplat.active).isEqualTo(true)
    }
}