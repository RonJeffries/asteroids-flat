package com.ronjeffries.flat

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

// Globals

var controls_left: Boolean = false
var controls_right: Boolean = false
var controls_accelerate: Boolean = false
var controls_fire: Boolean = false
var controls_hyperspace: Boolean = false

const val Width = 1024
const val Height = 1024
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
        if (spaceObject.type == SpaceObjectType.MISSILE){
            tickTimer(spaceObject, deltaTime)
        }
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer)
    }
}

fun tickTimer(spaceObject: SpaceObject, deltaTime: Double) {
    if (spaceObject.timer > 0) {
        spaceObject.timer -= deltaTime
        if (spaceObject.timer <= 0.0) spaceObject.active = false
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
    val offset = Vector2(50.0, 0.0).rotate(Ship.angle)
    missile.x = offset.x + Ship.x
    missile.y = offset.y + Ship.y
    val velocity = Vector2(166.6, 0.0).rotate(Ship.angle)
    missile.dx = velocity.x + Ship.dx
    missile.dy = velocity.y + Ship.dy
    missile.timer = 3.0
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

fun createGame(missileCount: Int, asteroidCount: Int) {
    val objects = mutableListOf<SpaceObject>()
    for ( i in 1..missileCount) {
        objects.add(newMissile())
    }
    Ship = newShip()
    objects.add(Ship)
    spaceObjects = objects.toTypedArray()
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
