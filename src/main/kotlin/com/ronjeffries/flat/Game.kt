package com.ronjeffries.flat

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

// Globals

const val Width = 1024
const val Height = 1023
lateinit var spaceObjects: Array<SpaceObject>
lateinit var Ship: SpaceObject

fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    for (spaceObject in spaceObjects) {
        if (spaceObject.type == SpaceObjectType.SHIP) {
            applyControls(spaceObject, deltaTime)
        }
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer)
    }
}

private fun applyControls(spaceObject: SpaceObject, deltaTime: Double) {
    if (controls_left) spaceObject.angle -= 250.0 * deltaTime
    if (controls_right) spaceObject.angle += 250.0 * deltaTime
    if (controls_accelerate) {
        val deltaV = Vector2(120.0, 0.0).rotate(spaceObject.angle) * deltaTime
        spaceObject.dx += deltaV.x
        spaceObject.dy += deltaV.y
    }
    if (controls_fire) fireMissile()
}

fun fireMissile() {
    controls_fire = false
    val missile: SpaceObject = availableShipMissile() ?: return
    missile.x = Ship.x+ 50.0
    missile.y = Ship.y
    missile.dx = 15.0
    missile.dy = 0.0
    missile.active = true
}

fun availableShipMissile(): SpaceObject? {
    for ( i in 2..5) {
        if (!spaceObjects[i].active) {
            return spaceObjects[i]
        }
    }
    return null
}

fun createInitialObjects(missileCount: Int, asteroidCount: Int): Array<SpaceObject> {
    val objects = mutableListOf<SpaceObject>()
    for ( i in 1..missileCount) {
        objects.add(newMissile())
    }
    Ship = newShip()
    objects.add(Ship)
    return objects.toTypedArray()
}

fun startGame() {
    Ship.active = true
    Ship.x = Width/2 + 0.0
    Ship.y = Height/2 + 0.0
}

private fun newMissile(): SpaceObject
    = SpaceObject(SpaceObjectType.MISSILE, 0.0, 0.0, 0.0, 0.0, 0.0, false)

private fun newShip(): SpaceObject
    = SpaceObject(SpaceObjectType.SHIP, 0.0, 0.0, 0.0, 0.0, 0.0, false)