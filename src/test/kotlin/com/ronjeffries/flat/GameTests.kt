package com.ronjeffries.flat

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class GameTests {
    @Test
    fun `game starts with four asteroids`() {
        createGame(U.ShipMissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(4)
    }

    @Test
    fun `check game creates requested number of items`() {
        val saucerMissileCount = 3
        val shipMissileCount = 5
        val asteroidCount = 7
        createGame(saucerMissileCount, shipMissileCount, asteroidCount)
        assertThat(saucerMissiles(SpaceObjects).size).isEqualTo(saucerMissileCount)
        assertThat(shipMissiles(SpaceObjects).size).isEqualTo(shipMissileCount)
        assertThat(asteroids(SpaceObjects).size).isEqualTo(asteroidCount)
    }

    @Test
    fun `second wave is six`() {
        createGame(U.ShipMissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        clearAsteroids()
        checkIfNewAsteroidWaveNeeded(0.1)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(0)
        checkIfNewAsteroidWaveNeeded(U.AsteroidWaveDelay+0.1)
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
        createGame(U.ShipMissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        clearAsteroids()
        checkIfNewAsteroidWaveNeeded(0.1)
        checkIfNewAsteroidWaveNeeded(4.1)
        assertThat(activeAsteroids(SpaceObjects).size).isEqualTo(6)
        activeAsteroids(SpaceObjects).forEach {
            assertThat(it.scale).isEqualTo(4.0)
        }
    }

    @Test
    fun `ship refresh`() {
        createGame(U.ShipMissileCount, U.AsteroidCount)
        startGame(U.ScreenWidth, U.ScreenHeight)
        Ship.active = false
        updateTimers(0.1)
        updateTimers(U.ShipDelay + 0.1)
        assertThat(Ship.active).isEqualTo(true)
        assertThat(dropScale).isEqualTo(U.ShipDropInScale)
    }

    @Test
    fun `ship friction`() {
        createGame(0,0)
        Ship.velocity = Vector2(2.0, 0.0)
        applyControls(Ship, 1.0)
        assertThat(Ship.dx).isEqualTo(1.0, within(0.1))
    }

    @Test
    fun `safeToEmerge detects saucer`() {
        createGame(4,4)
        val timer = Timer(Ship, U.ShipDelay) {}
        assertThat(safeToEmerge(timer)).isEqualTo(true)
        Saucer.active = true
        assertThat(safeToEmerge(timer)).isEqualTo(false)
    }

    @Test
    fun `safeToEmerge detects missiles`() {
        createGame(4,4)
        val timer = Timer(Ship, U.ShipDelay) {}
        assertThat(safeToEmerge(timer)).isEqualTo(true)
        withAvailableMissile { missile-> missile.active = true }
        assertThat(safeToEmerge(timer)).isEqualTo(false)
    }

    @Test
    fun `safeToEmerge detects saucer missiles`() {
        createGame(4,4,4)
        val timer = Timer(Ship, U.ShipDelay) {}
        assertThat(safeToEmerge(timer)).isEqualTo(true)
        withAvailableSaucerMissile { missile-> missile.active = true }
        assertThat(safeToEmerge(timer)).isEqualTo(false)
    }

    @Test
    fun `safeToEmerge detects close asteroids`() {
        createGame(4,4)
        val timer = Timer(Ship, U.ShipDelay) {}
        assertThat(safeToEmerge(timer)).isEqualTo(true)
        activateAsteroids(1)
        val asteroid = activeAsteroids(SpaceObjects).first()
        asteroid.position = U.CenterOfUniverse + Vector2(50.0, 50.0)
        assertThat(safeToEmerge(timer)).isEqualTo(false)
    }

    private fun clearAsteroids() {
        activeAsteroids(SpaceObjects).forEach {
            it.active = false
            it.scale = 1.0
        }
    }
}
