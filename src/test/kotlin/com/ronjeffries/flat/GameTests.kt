package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
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

    private fun clearAsteroids() {
        activeAsteroids(SpaceObjects).forEach {
            it.active = false
            it.scale = 1.0
        }
    }
}
