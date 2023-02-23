package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class CollisionTests {
    @Test
    fun `asteroid and missile far apart do not collide`() {
        val asteroid = newAsteroid()
        asteroid.position = Vector2(100.0, 100.0)
        val missile :SpaceObject = newMissile()
        missile.position = Vector2(200.0,200.0)
        assertThat(colliding(missile, asteroid)).isEqualTo(false)
    }

    @Test
    fun `asteroid and missile close enough do collide`() {
    }

    @Test
    fun `when asteroid and missile collide, missile goes inactive`() {
    }

    @Test
    fun `when colliding asteroid size is 1, it goes inactive`() {
    }

    @Test
    fun `when colliding asteroid size exceeds 1, it gets smaller`() {
    }

    @Test
    fun `when colliding asteroid size exceeds 1, a new asteroid appears`() {
    }

    @Test
    fun `new asteroids get new velocity`() {
    }
}