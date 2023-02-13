package com.ronjeffries.flat

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

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

private fun fireMissile() {
    controls_fire = false
    val ship = spaceObjects[1]
    val missile = spaceObjects[2]
    missile.x = ship.x+ 50.0
    missile.y = ship.y
    missile.dx = 15.0
    missile.dy = 0.0
    missile.active = true
}