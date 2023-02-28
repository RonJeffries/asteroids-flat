package com.ronjeffries.flat

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class GameTests {
    @Test
    fun `game starts with four asteroids`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(4)
    }

    @Test
    fun `second wave is six`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        clearAsteroids()
        checkIfAsteroidsNeeded(0.1)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(0)
        checkIfAsteroidsNeeded(U.AsteroidWaveDelay+0.1)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(6)
    }

    @Test
    fun `check wave sizes`() {
        assertThat(nextWaveSize(4)).isEqualTo(6)
        assertThat(nextWaveSize(6)).isEqualTo(8)
        assertThat(nextWaveSize(8)).isEqualTo(10)
        assertThat(nextWaveSize(10)).isEqualTo(11)
        assertThat(nextWaveSize(11)).isEqualTo(11)
    }

    @Test
    fun `new wave is full size`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        clearAsteroids()
        checkIfAsteroidsNeeded(0.1)
        checkIfAsteroidsNeeded(4.1)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(6)
        activeAsteroids(SpaceObjects).forEach {
            assertThat(it.scale).isEqualTo(4.0)
        }
    }

    @Test
    fun `ship refresh`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        Ship.active = false
        checkIfShipNeeded(0.1)
        checkIfShipNeeded(U.ShipDelay + 0.1)
        assertThat(Ship.active).isEqualTo(true)
        assertThat(dropScale).isEqualTo(U.ShipDropInScale)
        checkIfShipNeeded(1.1)
        assertThat(dropScale).isEqualTo(1.0)
    }

    @Test
    fun `ship friction`() {
        createGame(0,0)
        Ship.velocity = Vector2(2.0, 0.0)
        applyControls(Ship, 1.0)
        assertThat(Ship.dx).isEqualTo(1.0, within(0.1))
    }

    @Test
    fun `start saucer after seven seconds`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        assertThat(Saucer.active).isEqualTo(false)
        checkIfSaucerNeeded(0.1)
        assertThat(Saucer.active).isEqualTo(false)
        checkIfSaucerNeeded(U.SaucerDelay + 0.1)
        assertThat(Saucer.active).isEqualTo(true)
        checkIfSaucerNeeded(U.SaucerDelay + 0.1)
        assertThat(Saucer.active).isEqualTo(false)
        checkIfSaucerNeeded( 0.1)
        assertThat(Saucer.active).describedAs("stays gone").isEqualTo(false)
    }

    @Test
    fun `saucer switches direction`() {
        createGame(U.MissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        checkIfSaucerNeeded(U.SaucerDelay + 0.1)
        assertThat(Saucer.active).isEqualTo(true)
        assertThat(Saucer.dx).isGreaterThan(0.0)
        checkIfSaucerNeeded(U.SaucerDelay + 0.1)
        assertThat(Saucer.active).isEqualTo(false)
        checkIfSaucerNeeded(U.SaucerDelay + 0.1)
        assertThat(Saucer.active).isEqualTo(true)
        assertThat(Saucer.dx).describedAs("reverse").isLessThan(0.0)
    }

    private fun clearAsteroids() {
        activeAsteroids(SpaceObjects).forEach {
            it.active = false
            it.scale = 1.0
        }
    }
}
