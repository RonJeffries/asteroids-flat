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
}