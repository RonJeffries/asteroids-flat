package com.ronjeffries.flat

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test

class InitialTests {
    @Test
    fun `hook up`() {
        assertThat(1+1).isEqualTo(2)
    }

    @Test
    fun `move an asteroid`() {
        val spaceObject = SpaceObject(SpaceObjectType.ASTEROID, 100.0, 100.0, 10.0, -10.0)
        move(spaceObject, 200.0, 200.0, 0.5)
        assertThat(spaceObject.x).isEqualTo(105.0, within(0.01))
        assertThat(spaceObject.y).isEqualTo(95.0, within(0.01))
    }

    @Test
    fun `asteroid wraps high`() {
        val spaceObject = SpaceObject(SpaceObjectType.ASTEROID,95.0, 98.0, 10.0, 10.0)
        move(spaceObject, 100.0, 100.0, 1.0)
        assertThat(spaceObject.x).isEqualTo(5.0, within(0.01))
        assertThat(spaceObject.y).isEqualTo(8.0, within(0.01))
    }

    @Test
    fun `asteroid wraps low`() {
        val spaceObject = SpaceObject(SpaceObjectType.ASTEROID, 5.0, 8.0, -10.0, -10.0,)
        move(spaceObject, 100.0, 100.0, 1.0)
        assertThat(spaceObject.x).isEqualTo(95.0, within(0.01))
        assertThat(spaceObject.y).isEqualTo(98.0, within(0.01))
    }

    @Test
    fun `initial array creation`() {
        createGame(6, 26) // number of missiles, number of asteroids
        val mCount = SpaceObjects.count { it.type == SpaceObjectType.MISSILE}
        assertThat(mCount).isEqualTo(6)
        assertThat(Ship.type).isEqualTo(SpaceObjectType.SHIP)
        assertThat(Ship).isEqualTo(SpaceObjects[6])
    }

    @Test
    fun `start game makes ship active`() {
        createGame(6, 26)
        assertThat(Ship.active).isEqualTo(false)
        startGame(500, 600)
        assertThat(Ship.active).isEqualTo(true)
        assertThat(Ship.x).isEqualTo(250.0)
        assertThat(Ship.y).isEqualTo(300.0)
    }

    @Test
    fun `can fire four missiles`() {
        createGame(6, 26)
        assertThat(activeMissileCount()).isEqualTo(0)
        fireMissile()
        assertThat(activeMissileCount()).isEqualTo(1)
        fireMissile()
        assertThat(activeMissileCount()).isEqualTo(2)
        fireMissile()
        assertThat(activeMissileCount()).isEqualTo(3)
        fireMissile()
        assertThat(activeMissileCount()).isEqualTo(4)
        fireMissile()
        assertThat(activeMissileCount()).isEqualTo(4)
        val missile = SpaceObjects.find { it.type == SpaceObjectType.MISSILE && it.active == true}
        TimerTable.forEach {updateTimer(it, 3.1)}
        assertThat(activeMissileCount()).describedAs("reactivating").isEqualTo(0)
    }

    @Test
    fun `can activate asteroids`() {
        createGame(6, 6)
        val asteroidCount = SpaceObjects.count { it.type == SpaceObjectType.ASTEROID}
        assertThat(asteroidCount).isEqualTo(6)
        var inactiveCount = SpaceObjects.count { it.type == SpaceObjectType.ASTEROID && ! it.active}
        assertThat(inactiveCount).isEqualTo(6)
        for ( i in 1..4) activateAsteroidAtEdge()
        inactiveCount = SpaceObjects.count { it.type == SpaceObjectType.ASTEROID && ! it.active}
        assertThat(inactiveCount).isEqualTo(2)
    }

    private fun activeMissileCount(): Int {
        return SpaceObjects.count { it.type == SpaceObjectType.MISSILE && it.active == true}
    }
}
